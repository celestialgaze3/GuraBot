package net.celestialgaze.GuraBot.commands;

import java.util.Random;

import net.celestialgaze.GuraBot.OpenSimplexNoise;
import net.celestialgaze.GuraBot.SharkUtil;
import net.celestialgaze.GuraBot.commands.classes.Command;
import net.dv8tion.jda.api.entities.Message;

public class ShipCommand extends Command {
	public ShipCommand(String name, String usage, String description) {
		super(name, usage, description);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {}

	@Override
	protected void run(Message message, String[] args) {
		if (args.length < 2) {
			message.getChannel().sendMessage("Please input at least 2 arguments");
			return;
		}

		String ship = (args[0] + args[1]).toLowerCase();
		OpenSimplexNoise noise = new OpenSimplexNoise(929465298);
		
		if (args.length == 3) {
			if (args[2].startsWith("--brute-force")) { // If the user is bruteforcing
				long time = System.currentTimeMillis();
				message.getChannel().sendMessage("Calculating...").queue(response -> {
					boolean lowest = args[2].endsWith("-lowest");
					Random random2 = new Random();
					double highest = (lowest ? 100 : 0);
					int highestSeed = 0;
					for (int i = 0; i < 9999; i++) {
						int seed = random2.nextInt(Integer.MAX_VALUE);
						OpenSimplexNoise _noise = new OpenSimplexNoise(seed);
						Random random = new Random();
						int x = 0, y = 0;
						for (int j = 0; j < ship.length(); j++) {
							int value = Math.toIntExact(Math.round(toPercent(_noise.eval(ship.charAt(j)*100, 10))));
							x += value;
							y += value;
						}
						random.setSeed(Math.toIntExact(Math.round((_noise.eval(x, y)*Integer.MAX_VALUE))));
						double shipPercent = random.nextDouble()*100;
						if (!lowest && shipPercent > highest) {
							highest = shipPercent;
							highestSeed = seed;
						} else if (lowest && shipPercent < highest) {
							highest = shipPercent;
							highestSeed = seed;
						}
					}
					response.editMessage("Brute-force revealed a result of **" + String.format("%.2f", highest) + "%** with seed `" + 
								highestSeed + "` (" + (System.currentTimeMillis()-time) +"ms)").queue();
				});
				return;
			} else {
				try {
					noise = new OpenSimplexNoise(Integer.parseInt(args[2]));
				} catch (NumberFormatException e) {
					SharkUtil.error(message, "User Error (you did something wrong): NumberFormatException " + e.getMessage());
					return;
				}
			}
		}
		Random random = new Random();
		int x = 0, y = 0;
		for (int i = 0; i < ship.length(); i++) {
			int value = Math.toIntExact(Math.round(toPercent(noise.eval(ship.charAt(i)*100, 10))));
			x += value;
			y += value;
		}
		random.setSeed(Math.toIntExact(Math.round((noise.eval(x, y)*Integer.MAX_VALUE))));
		double shipPercent = random.nextDouble()*100;
		String shipPercentString = String.format("%.2f", shipPercent);
		
		message.getChannel().sendMessage(format(args, shipPercentString)).queue();
	}
	
	private double toPercent(double noiseValue) {
		return (noiseValue+1)*50; // starts w/ range from -1 to 1. +1 changes it to from 0 to 2, x50 changes it from 0 to 100
	}
	
	private String format(String[] args, String percentString) {
		String enclosing1 = (!user(args[0]) ? "`" : "");
		String enclosing2 = (!user(args[1]) ? "`" : "");
		return (enclosing1 + args[0] + enclosing1 + " ❤︎ " + enclosing2 + args[1] + enclosing2 +": **" + percentString + "%**");
	}
	
	private boolean user(String str) {
		return str.startsWith("<@!") && str.endsWith(">");
	}

}
