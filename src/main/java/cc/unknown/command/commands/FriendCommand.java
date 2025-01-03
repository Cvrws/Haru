package cc.unknown.command.commands;

import java.util.ArrayList;

import cc.unknown.command.Command;
import cc.unknown.utils.player.FriendUtil;
import net.minecraft.entity.Entity;

public class FriendCommand extends Command {
	
	public FriendCommand() {
		super("Friend", "It allows you to save a friend", "fr", ".friend add/remove <name>");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onExecute(String[] args) {
	    if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
	        listFriends();
	    } else if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
	        Entity friendEntity = findEntity(args[1]);
	        if (friendEntity != null) {
	            if (args[0].equalsIgnoreCase("add")) {
	                addFriend(friendEntity);
	            } else {
	                removeFriend(friendEntity);
	            }
	        } else {
	            this.sendChat(getColor("Red") + " Player not found.");
	        }
	    } else {
	        this.sendChat(getColor("Red") + " Syntax Error.");
	    }
	}

	private void listFriends() {
		ArrayList<Entity> friends = FriendUtil.getFriends();
	    if (friends.isEmpty()) {
	        this.sendChat(getColor("Gray") + " You have no friends. :(");
	    } else {
	        this.sendChat(getColor("Gray") + " Your friends are:");
	        friends.stream().map(Entity::getName).forEach(name -> this.sendChat(getColor("Gray") + name));
	    }
	}

	private void addFriend(Entity friendEntity) {
		FriendUtil.addFriend(friendEntity);
	    this.sendChat(getColor("Gray") + " New friend " + friendEntity.getName() + " :)");
	}

	private void removeFriend(Entity friendEntity) {
	    boolean removed = FriendUtil.removeFriend(friendEntity);
	    if (removed) {
	        this.sendChat(getColor("Gray") + " Successfully removed " + friendEntity.getName() + " from your friends list!");
	    }
	}
	
	private Entity findEntity(String name) {
	    return mc.theWorld.getLoadedEntityList().stream().filter(entity -> entity.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
}
