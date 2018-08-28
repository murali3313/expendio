package com.thriwin.expendio;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static com.thriwin.expendio.Utils.EXPENDIO_SMS;
import static com.thriwin.expendio.Utils.isNull;

@Setter
@Getter
public class ShareSettings {

    private SHARE_TYPE shareType;

    private List<User> users = new ArrayList<>();

    @JsonIgnore
    public boolean isSMS() {
        return !isNull(shareType) && shareType.equals(SHARE_TYPE.SMS);
    }

    @JsonIgnore
    public List<User> getAllUsers() {
        return users;
    }

    @JsonIgnore
    public List<User> getAllSMSUsers() {
        List<User> smsUsers = new ArrayList<>();
        for (User user : users) {
            if (user.hasPhoneNumber()) {
                smsUsers.add(user);
            }
        }
        return smsUsers;
    }

    @JsonIgnore
    public List<User> getAllBluetoothUsers() {
        List<User> bluetoothUsers = new ArrayList<>();
        for (User user : users) {
            if (user.hasPairDetails()) {
                bluetoothUsers.add(user);
            }
        }
        return bluetoothUsers;
    }


    public void addSMSUser(User newUser) {
        User user = null;
        for (User existingUser : users) {
            if (existingUser.getName().equalsIgnoreCase(newUser.getName())) {
                user = existingUser;
                break;
            }
        }
        if (!isNull(user)) {
            user.setNumber(newUser.getNumber());
        } else {
            this.users.add(newUser);
        }
    }

    public void addBluetoothUser(User newUser) {
        User user = null;
        for (User existingUser : users) {
            if (existingUser.getName().equalsIgnoreCase(newUser.getName())) {
                user = existingUser;
                break;
            }
        }
        if (!isNull(user)) {
            user.setPairDetail(newUser.getPairDetail());
            user.setPairDeviceName(newUser.getPairDeviceName());
        } else {
            this.users.add(newUser);
        }
    }


    public User getAuthenticatedSMSUser(String from, String message) {
        if (!message.contains(EXPENDIO_SMS)) {
            return null;
        }
        for (User user : getAllSMSUsers()) {
            if (user.isSMSFrom(from)) {
                return user;
            }
        }
        return null;
    }

    public User getAuthenticatedBluetoothUser(String from, String message) {
        if (!message.contains(EXPENDIO_SMS)) {
            return null;
        }
        for (User user : getAllBluetoothUsers()) {
            if (user.isMessageFrom(from)) {
                return user;
            }
        }
        return null;
    }

    public Expenses getParsedExpenses(String expenseStringFromMessage) {
        String[] expensesMessage = expenseStringFromMessage.split("&");
        Expenses expenses = new Expenses();
        for (String expens : expensesMessage) {
            Expense parse = Expense.parse(expens);
            if (!isNull(parse)) {
                expenses.add(parse);
            }
        }
        return expenses;
    }

    public List<String> compare(ShareSettings shareSettings) {
        List<String> affectedUsers = new ArrayList<>();
        for (User user : shareSettings.getAllUsers()) {
            User userFromThis = this.getUser(user.getName());
            if (isNull(userFromThis)) {
                affectedUsers.add(user.getName());
            }
        }
        return affectedUsers;
    }

    private User getUser(String name) {
        for (User user : users) {
            if (user.sameName(name)) {
                return user;
            }
        }
        return null;
    }
}
