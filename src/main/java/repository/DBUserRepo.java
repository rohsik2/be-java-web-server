package repository;

import model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DBUserRepo implements UserRepo{
    @Override
    public void addUser(User user) {
        String[] args = {user.getUserId(), user.getPassword(), user.getName(), user.getEmail()};
        DBConnectionManager.sendSql("insert into Customer(id, password, name, email) values (?, ?, ?, ?)", args);
    }

    @Override
    public Optional<User> findUserById(String userId) {
        String[] args = {userId};
        List<List<String>> users = DBConnectionManager.sendSql("select id, password, name, email from Customer where id=?", args);
        User user = null;
        if(users.size() > 0){
            user = new User(users.get(0));
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Collection<User> findAll() {
        List<List<String>> users = DBConnectionManager.sendSql("select id, password, name, email from Customer", null);
        List<User> userList = new ArrayList<>();
        for(List<String> row : users){
            User user = new User(row);
            userList.add(user);
        }
        return userList;
    }
}
