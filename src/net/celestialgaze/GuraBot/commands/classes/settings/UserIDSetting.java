package net.celestialgaze.GuraBot.commands.classes.settings;

import net.celestialgaze.GuraBot.commands.classes.CommandModule;
import net.celestialgaze.GuraBot.commands.classes.CommandModuleSetting;
import net.celestialgaze.GuraBot.util.SharkUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class UserIDSetting extends CommandModuleSetting<Long> {

	public UserIDSetting(CommandModule module, String name, long defaultValue) {
		super(module, name, defaultValue);
	}

	@Override
	public boolean validate(Guild guild, Long newValue) {
		return guild.getMemberById(newValue) != null || newValue == 0;
	}

	@Override
	protected String getInvalidMessage(Guild guild, Long invalidValue) {
		return invalidValue + " is not the ID of a member within " + guild.getName() + ", so I've reset the value of " + 
				name + " in the `" + module.getName() + "` module";
	}

	@Override
	protected String display(Guild guild, Long value) {
		return (value == 0 ? "None selected" : "<@!"+value+">");
	}

	@Override
	public boolean trySet(Guild guild, String input) {
		Member member = guild.getMemberById(SharkUtil.getIdFromMention(input));
		if (member != null) {
			this.set(guild, member.getIdLong());
			return true;
		}
		return false;
	}

	@Override
	public String getInvalidInputMessage(Guild guild, String invalidInput) {
		return "Input must be a mention or an ID of an existing member.";
	}
}
