package de.hotmail.gurkilein.bankcraft.banking;

import org.bukkit.entity.Player;

import de.hotmail.gurkilein.bankcraft.Bankcraft;

public class MoneyBankingHandler implements BankingHandler<Double>{
	
	private Bankcraft bankcraft;

	public MoneyBankingHandler(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
	}

	@Override
	public boolean transferFromPocketToAccount(Player pocketOwner,
			String accountOwner, Double amount, Player observer) {
		
		if (Bankcraft.econ.getBalance(pocketOwner.getName()) >= amount && bankcraft.getMoneyDatabaseInterface().getBalance(accountOwner)<= Double.MAX_VALUE-amount) {
			if (bankcraft.getMoneyDatabaseInterface().getBalance(accountOwner)<= Double.MAX_VALUE-amount) {
				Bankcraft.econ.withdrawPlayer(pocketOwner.getName(), amount);
				bankcraft.getMoneyDatabaseInterface().addToAccount(accountOwner, amount);
				bankcraft.getConfigurationHandler().printMessage(observer, "message.depositedSuccessfully", amount+"", accountOwner);
				return true;
			} else {
				bankcraft.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInAccount", amount+"", accountOwner);
			}
		} else {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInPoket", amount+"", pocketOwner.getName());
		}
		return false;
	}

	@Override
	public boolean transferFromAccountToPocket(String accountOwner,
			Player pocketOwner, Double amount, Player observer) {
		
		if (bankcraft.getMoneyDatabaseInterface().getBalance(accountOwner) >= amount) {
			if (Bankcraft.econ.getBalance(pocketOwner.getName())<= Double.MAX_VALUE-amount) {
				bankcraft.getMoneyDatabaseInterface().removeFromAccount(accountOwner, amount);
				Bankcraft.econ.depositPlayer(pocketOwner.getName(), amount);
				bankcraft.getConfigurationHandler().printMessage(observer, "message.withdrewSuccessfully", amount+"", accountOwner);
				return true;
			} else {
				bankcraft.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInPocket", amount+"", pocketOwner.getName());
			}
		} else {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInAccount", amount+"", accountOwner);
		}
		return false;
	}

	@Override
	public boolean transferFromAccountToAccount(String givingPlayer,
			String gettingPlayer, Double amount, Player observer) {
		if (!bankcraft.getMoneyDatabaseInterface().hasAccount(gettingPlayer)) {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount+"", gettingPlayer);
			return false;
		}
		if (bankcraft.getMoneyDatabaseInterface().getBalance(givingPlayer) >= amount) {
			if (bankcraft.getMoneyDatabaseInterface().getBalance(gettingPlayer)<= Integer.parseInt(bankcraft.getConfigurationHandler().getString("general.maxBankLimit"))-amount) {
				bankcraft.getMoneyDatabaseInterface().removeFromAccount(givingPlayer, amount);
				bankcraft.getMoneyDatabaseInterface().addToAccount(gettingPlayer, amount);
				bankcraft.getConfigurationHandler().printMessage(observer, "message.transferedSuccessfully", amount+"", gettingPlayer);
				return true;
			} else {
				bankcraft.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInAccount", amount+"", gettingPlayer);
			}

		} else {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInAccount", amount+"", givingPlayer);
		}
		return false;
	}

	@Override
	public boolean grantInterests(Player observer) {
		String messageKey;
		for (String accountName: bankcraft.getMoneyDatabaseInterface().getAccounts()) {
			
			double interest = bankcraft.getConfigurationHandler().getInterestForPlayer(accountName, this);
			double amount = interest*bankcraft.getMoneyDatabaseInterface().getBalance(accountName);
			
			if (bankcraft.getMoneyDatabaseInterface().getBalance(accountName)<= Integer.parseInt(bankcraft.getConfigurationHandler().getString("general.maxBankLimit"))-amount) {
				bankcraft.getMoneyDatabaseInterface().addToAccount(accountName, amount);
				messageKey = "message.grantedInterestOnMoney";
			} else {
				messageKey = "message.couldNotGrantInterestOnMoney";
			}
			Player player;
			if (bankcraft.getConfigurationHandler().getString("interest.broadcastMoney").equals("true") && (player =bankcraft.getServer().getPlayer(accountName)) != null) {
				bankcraft.getConfigurationHandler().printMessage(player, messageKey, amount+"", player.getName());
			}
		}
		return true;
	}


}
