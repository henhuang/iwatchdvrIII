package peersdk;

import android.content.Context;
import refactor.remote.iWatchDVR.R;

/**
 * @brief Specifies type of system log.
 */
public class LogType {

    /**
     * Indicates a power on event.
     */
    public static final int PowerOn = 0;

    /**
     * Indicates a channel recording event.
     */
    public static final int RecordCH = 1;

    /**
     * Indicates a videoloss event.
     */
    public static final int VLoss = 2;

    /**
     * Indicates a sensor triggerred event.
     */
    public static final int Sensor = 3;

    /**
     * Indicates a video motion event.
     */
    public static final int Motion = 4;

    /**
     * Indicates a user login event.
     */
    public static final int Login = 5;

    /**
     * Indicates a user logout event.
     */
    public static final int Logout = 6;

    /**
     * Indicates a event that user exports or downloads system configuration.
     */
    public static final int ConfigExport = 7;

    /**
     * Indicates a event that user restores system configuration.
     */
    public static final int ConfigDefault = 8;

    /**
     * Indicates a event that user imports another system configuration.
     */
    public static final int ConfigImport = 9;

    /**
     * Indicates a event that user exports system log.
     */
    public static final int LogExport = 10;

    /**
     * Indicates a event that user erases all system logs.
     */
    public static final int LogClear = 11;

    /**
     * Indicates a event that user changes system (peer) time.
     */
    public static final int ChangeDateTime = 12;

    /**
     * Indicates a event that user changes recording configuration.
     */
    public static final int ChangeRecordSetting = 13;

    /**
     * Indicates a event that user formats HDDs.
     */
    public static final int HDDFormat = 14;

    /**
     * Indicates a event that user changes HDD flags.
     */
    public static final int HDDSet = 15;

    /**
     * Indicates a event that user upgrade firmware.
     */
    public static final int Upgrade = 16;

    /**
     * Indicates a event that user perform a backup job.
     */
    public static final int Backup = 17;

    /**
     * Indicates a event that administrator password has changed.
     */
    public static final int ChangeAdminPass = 18;

    /**
     * Indicates no HDD in system event.
     */
    public static final int NoHDD = 19;

    /**
     * Indicates all HDDs are full.
     */
    public static final int HDDFull = 20;

    /**
     * Indicates a HDD error event.
     */
    public static final int HDDError = 21;

    /**
     * Indicates a MCU error event.
     */
    public static final int MCUError = 22;

    /**
     * Indicates a system error event.
     */
    public static final int SystemError = 23;

    /**
     * Indicates a event that system failed to write HDD.
     */
    public static final int WriteError = 24;

    /**
     * Indicates a event that system reboots.
     */
    public static final int Reboot = 25;
    
    public static String toString(Context context, int type) {
        
        switch (type) {
        case PowerOn:
            return context.getResources().getString(R.string.powerOn);
            
        case RecordCH:
            return context.getResources().getString(R.string.error);
            
        case VLoss:
            return context.getResources().getString(R.string.videoLoss);
            
        case Sensor:
            return context.getResources().getString(R.string.sensor);
            
        case Motion:
            return context.getResources().getString(R.string.videoMotion);
            
        case Login:
            return context.getResources().getString(R.string.userLogin);
            
        case Logout:
            return context.getResources().getString(R.string.userLogout);
            
        case ConfigExport:
            return context.getResources().getString(R.string.exportConfiguration);
            
        case ConfigDefault:
            return context.getResources().getString(R.string.resetConfiguration);
            
        case ConfigImport:
        	return context.getResources().getString(R.string.importConfiguration);

        case LogExport:
            return context.getResources().getString(R.string.exportLogs);
            
        case LogClear:
            return context.getResources().getString(R.string.clearLogs);

        case ChangeDateTime:
            return context.getResources().getString(R.string.changeDateTime);
            
        case ChangeRecordSetting:
            return context.getResources().getString(R.string.chageRecordConfig);
            
        case HDDFormat:
        	return context.getResources().getString(R.string.hddFormat);
        	
        case HDDSet:
        	return context.getResources().getString(R.string.setHddRecordFlag);
        	
        case Upgrade:
        	return context.getResources().getString(R.string.systemUpgrade);
        	
        case Backup:
        	return context.getResources().getString(R.string.backup);
        	
        case ChangeAdminPass:
        	return context.getResources().getString(R.string.setAdminPassword);
        	
        case NoHDD:
            return context.getResources().getString(R.string.noHdd);
            
        case HDDFull:
            return context.getResources().getString(R.string.hddFull);
            
        case HDDError:
            return context.getResources().getString(R.string.hddError);
            
        case MCUError:
            return context.getResources().getString(R.string.mcuError);
            
        case SystemError:
            return context.getResources().getString(R.string.systemError);
            
        case WriteError:
            return context.getResources().getString(R.string.writeError);
            
        case Reboot:
            return context.getResources().getString(R.string.reboot);
        }

        return "Undefined";
    }
}
