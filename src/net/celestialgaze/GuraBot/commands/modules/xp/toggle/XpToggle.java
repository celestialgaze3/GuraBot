package net.celestialgaze.GuraBot.commands.modules.xp.toggle;

import net.celestialgaze.GuraBot.commands.classes.Command;
import net.celestialgaze.GuraBot.commands.classes.CommandOptions;
import net.celestialgaze.GuraBot.commands.classes.SubHelpCommand;
import net.dv8tion.jda.api.Permission;

public class XpToggle extends SubHelpCommand {
	public XpToggle(Command parent) {
		super(new CommandOptions()
				.setName("toggle")
				.setDescription("Commands to manage which channels xp is enabled in")
				.setPermission(Permission.MANAGE_SERVER)
				.setUsablePrivate(false)
				.verify(),
				"XP Toggle", 
				parent);
		for (Command cmd : subcommands.values()) {
			commands.put(cmd.getName(), cmd);
		}
	}

	@Override
	public void init() {
		XpToggleMode mode = new XpToggleMode(this);
		XpToggleAdd add = new XpToggleAdd(this);
		XpToggleRemove remove = new XpToggleRemove(this);
		XpToggleList list = new XpToggleList(this);
		XpToggleClear clear = new XpToggleClear(this);
		subcommands.put(mode.getName(), mode);
		subcommands.put(add.getName(), add);
		subcommands.put(remove.getName(), remove);
		subcommands.put(list.getName(), list);
		subcommands.put(clear.getName(), clear);
	}
}
