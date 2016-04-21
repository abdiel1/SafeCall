package btcomm;

/**
 * Created by Kenneth on 3/24/2016.
 *
 * Class to represent a Bluetooth device
 */
public class DeviceItem {
    private String deviceName;
    private String address;
    private boolean connected;

    public DeviceItem(String deviceName, String address, String connected) {
        this.deviceName = deviceName;
        this.address = address;
        if(connected == "true"){
            this.connected = true;
        }
        else{
            this.connected = false;
        }
    }

    public String getAddress() {
        return address;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


}
