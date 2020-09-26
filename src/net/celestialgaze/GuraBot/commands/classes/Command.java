package net.celestialgaze.GuraBot.commands.classes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import net.celestialgaze.GuraBot.GuraBot;
import net.celestialgaze.GuraBot.json.BotInfo;
import net.celestialgaze.GuraBot.json.StatType;
import net.celestialgaze.GuraBot.util.DelayedRunnable;
import net.celestialgaze.GuraBot.util.SharkUtil;

public abstract class Command implements ICommand {
	protected String name;
	protected String usage;
	protected String description;
	protected List<Command> subcommands = new ArrayList<Command>();
	Permission permission = null;
	protected boolean usablePrivately = true;
	protected boolean needBotAdmin = false;
	protected double cooldownDuration = 0.5;
	protected Map<Long, DelayedRunnable> userCooldowns = new HashMap<Long, DelayedRunnable>();
	
	protected Command(String name, String usage, String description) {
		setValues(name, usage, description, false);
	}
	protected Command(String name, String usage, String description, boolean initialize) {
		setValues(name, usage, description, initialize);
	}
	
	private void setValues(String name, String usage, String description, boolean initialize) {
		this.name = name;
		this.usage = usage;
		this.description = description;
		System.out.println("Initializing " + name + " command");
		if (initialize) init();
	}
	
	public abstract void init();
	
	@Override
	public void attempt(Message message, String[] args, String[] modifiers) {
		if (canRun(message, false)) {
			try {
				run(message, args, modifiers); // Run first so any cooldown changes carry over
				
				DelayedRunnable runnable = new DelayedRunnable(() -> {
					userCooldowns.remove(message.getAuthor().getIdLong());
				}).execute(System.currentTimeMillis()+Math.round(cooldownDuration*1000));

				userCooldowns.put(message.getAuthor().getIdLong(), runnable);
			} catch (Exception e) {
				String argsString = "";
				for (String arg : args) {
					argsString += arg + " ";
				}
				argsString = argsString.trim();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace();
				e.printStackTrace(pw);
				String error = "Something went horribly wrong...\n" + sw.toString().substring(0, Integer.min(700, sw.toString().length())) +
						"Args: " + argsString + "... \nFull message: " + 
						message.getContentRaw().substring(0, Integer.min(message.getContentRaw().length(), 100));
				SharkUtil.error(message, error);
				BotInfo.addLongStat(StatType.ERRORS);
				if (message.getAuthor().getIdLong() != Long.parseLong("218525899535024129")) {
					message.getChannel().sendMessage("Reporting to cel...").queue(response -> {
						GuraBot.jda.getUserByTag("celestialgaze", "0001").openPrivateChannel().queue(channel -> {
							channel.sendMessage("fix ur bot").queue(crashMsg -> {
								SharkUtil.error(crashMsg, error);
								response.editMessage("Successfully reported to cel.").queue();
							});
						});
					});
				}
			}
		}
	}
	public boolean canRun(Message message) {
		return canRun(message, true);
	}
	public boolean canRun(Message message, boolean silent) {
		if (!usablePrivately && message.getChannelType().equals(ChannelType.PRIVATE)) {
			error(message, "Sorry, this command is not available in DMs.", silent);
			return false;
		}
		if (permission != null && message.getChannelType().equals(ChannelType.TEXT) && !message.getMember().hasPermission(permission)) {
			error(message, "You need the " + permission.getName() + " permission to use this command", silent);
			return false;
		}
		if (needBotAdmin && message.getAuthor().getIdLong() != Long.valueOf("218525899535024129")) {
			error(message, "You're not celestialgaze#0001!", silent);
			return false;
		}
		if (cooldownDuration > 0.0 && userCooldowns.containsKey(message.getAuthor().getIdLong())) {
			error(message, "You must wait " + 
					String.format("%.2f", userCooldowns.get(message.getAuthor().getIdLong()).getTimeRemaining()/1000.0) + " seconds", silent);
			return false;
		}
		return true;
	}
	
	protected abstract void run(Message message, String[] args, String[] modifiers);

	private static void error(Message message, String error, boolean silent) {
		if (!silent) SharkUtil.error(message, error);
	}
	
	public String getName() {
		return name;
	}
	public String getUsage() {
		return usage;
	}
	public String getDescription() {
		return description;
	}
	public boolean isUsablePrivately() {
		return usablePrivately;
	}
	public boolean hasPermission() {
		return permission != null;
	}
	public Permission getPermission() {
		return permission;
	}
	
	public List<Command> getSubcommands() {
		return subcommands;
	}
}
