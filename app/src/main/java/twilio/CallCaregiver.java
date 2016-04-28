/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package twilio;

import android.content.Context;

import com.twilio.client.Connection;
import com.twilio.client.Device;

import java.util.Map;

public class CallCaregiver
{
    private TwilioPhone phone;
    private static CallCaregiver callCaregiver;

    private CallCaregiver(Context context)
    {
        phone = new TwilioPhone(context);
    }

    public static CallCaregiver getInstance(Context context){
        if(callCaregiver == null){
            callCaregiver = new CallCaregiver(context);
        }
        return callCaregiver;
    }

    public Device connect(Map<String, String> params)
    {
        return phone.connect(params);
    }

    public void disconnect()
    {
        phone.disconnect();
    }
}