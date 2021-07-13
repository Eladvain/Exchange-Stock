package UserManager;

import GenratedCode.RseUser;
import GenratedCode.RseUsers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Users implements Serializable {
    private List<User> listOfUsers;
    public Users(RseUsers users)
    {
        listOfUsers =new ArrayList<>();
        List<RseUser> genUserList = users.getRseUser();
        for(RseUser user :genUserList)
        {
            listOfUsers.add( new User((user)));
        }
    }
    public Users()
    {
        listOfUsers = new ArrayList<>();
    }
    public boolean addNewUserToList(User user)
    {
        for(User newUser : listOfUsers)
        {
            if(newUser.getName().equals(user.getName()))
                return  false;
        }
        listOfUsers.add(user);
        return  true;
    }
    public List<User> getListOfUsers() {
        return listOfUsers;
    }
}
