package com.thriwin.expendio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.thriwin.expendio.Utils.EXPENDIO_SMS;
import static com.thriwin.expendio.Utils.EXPENDIO_SMS_END;
import static com.thriwin.expendio.Utils.EXPENDIO_SMS_END_WITHESC;
import static com.thriwin.expendio.Utils.EXPENDIO_SMS_START;
import static com.thriwin.expendio.Utils.EXPENDIO_SMS_START_WITHESC;
import static com.thriwin.expendio.Utils.EXPENDIO_SMS_WITHESC;
import static com.thriwin.expendio.Utils.isNull;

public class SMSUserMessages {
    public HashMap<String, List<String>> smsMessagesPerUser = new HashMap<>();

    public void add(String user, String message) {
        List<String> messages = smsMessagesPerUser.get(user);
        if (isNull(messages)) {
            smsMessagesPerUser.put(user, new ArrayList<String>() {{
                add(message);
            }});
        } else {
            messages.add(message);
        }
    }

    public boolean isAllMessagesComplete(String name) {
        List<String> messages = smsMessagesPerUser.get(name);
        boolean isSMSStartFound = false;
        boolean isSMSEndFound = false;

        for (String message : messages) {
            if (message.contains(EXPENDIO_SMS_START)) {
                isSMSStartFound = true;
            }
            if (message.contains(EXPENDIO_SMS_END)) {
                isSMSEndFound = true;
            }
        }
        return isSMSEndFound && isSMSStartFound;
    }

    public String getCollatedMessages(String name) {
        List<String> messages = smsMessagesPerUser.get(name);
        StringBuilder stringBuilder = new StringBuilder();
        for (String message : messages) {
           stringBuilder.append(message.replaceAll(EXPENDIO_SMS_END_WITHESC,"").replaceAll(EXPENDIO_SMS_START_WITHESC,"").replaceAll(EXPENDIO_SMS_WITHESC,""));
        }
        smsMessagesPerUser.remove(name);
        return stringBuilder.toString();
    }
}
