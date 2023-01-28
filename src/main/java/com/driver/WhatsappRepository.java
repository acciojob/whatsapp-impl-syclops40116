package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    
    public String createUser(String name, String mobile) throws Exception {
    	if(userMobile.contains(mobile))
    		throw new Exception("User already exists");
    	
    	userMobile.add(mobile);
    	return "SUCCESS";
    }
    
    public Group createGroup(List<User> users) {
    	if(users.size() == 2) {
    		String groupName = users.get(1).getName();
    		Group personalGroup = new Group(groupName, 2);
    		groupUserMap.put(personalGroup, users);
    		return personalGroup;
    	}
    	this.customGroupCount++;
    	String groupName = "Group " + this.customGroupCount;
        Group group = new Group(groupName, users.size());
        groupUserMap.put(group, users);
        adminMap.put(group, users.get(0));
        return group;
    }
    
    public int createMessage(String content){
        this.messageId++;
        Message message = new Message(messageId, content, new Date());
        return this.messageId;
    }
    
    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)) throw new Exception("Group does not exist");
        if(!this.userExistsInGroup(group, sender)) throw  new Exception("You are not allowed to send message");

        List<Message> messages = new ArrayList<>();
        if(groupMessageMap.containsKey(group)) messages = groupMessageMap.get(group);

        messages.add(message);
        groupMessageMap.put(group, messages);
        return messages.size();
    }
    
    public boolean userExistsInGroup(Group group, User sender) {
        List<User> users = groupUserMap.get(group);
        for(User user: users) {
            if(user.equals(sender)) return true;
        }

        return false;
    }
    
    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)) {
        	throw new Exception("Group does not exist");        	
        }
        if(!adminMap.get(group).equals(approver)) {
        	throw new Exception("Approver does not have rights");
        }
        if(!this.userExistsInGroup(group, user)) {
        	throw  new Exception("User is not a participant");
        }

        adminMap.put(group, user);
        return "SUCCESS";
    }
}
