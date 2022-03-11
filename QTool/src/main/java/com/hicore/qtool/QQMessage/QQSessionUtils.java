package com.hicore.qtool.QQMessage;

import android.os.Bundle;
import android.text.TextUtils;

import com.hicore.HookItem;
import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.XposedInit.HostInfo;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Field;

public class QQSessionUtils {
    private static final String TAG = "QQSessionUtils";
    public static Object Build_SessionInfo(String GroupUin,String UserUin){
        try{
            if(GroupUin.contains("&")){
                String[] Cut = GroupUin.split("&");
                if(Cut.length>1){
                    return Build_SessionInfo_Guild(Cut[0],Cut[1],!UserUin.isEmpty());
                }
                return null;
            }
            Object mObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"));
            if(TextUtils.isEmpty(UserUin))
            {
                Table_Session_Field.isTroop().set(mObj,1);
                Table_Session_Field.friendUin().set(mObj,GroupUin);
                Table_Session_Field.TroopCode().set(mObj,GroupUin);

            }
            else if (TextUtils.isEmpty(GroupUin))
            {
                Table_Session_Field.isTroop().set(mObj,0);
                Table_Session_Field.friendUin().set(mObj,UserUin);
            }
            else
            {
                Table_Session_Field.isTroop().set(mObj,1000);
                Table_Session_Field.friendUin().set(mObj,UserUin);
                Table_Session_Field.InTroopUin().set(mObj,GroupUin);
                Table_Session_Field.TroopCode().set(mObj,GroupUin);
            }
            return mObj;
        }catch (Exception e){
            LogUtils.error(TAG,"Can't not build session:\n"+e);
            return null;
        }
    }
    public static String getGroupUin(){
        try {
            int SessionType = (int) Table_Session_Field.isTroop().get(HookEnv.SessionInfo);
            if(SessionType==0)return "";
            if(SessionType==1)return (String) Table_Session_Field.friendUin().get(HookEnv.SessionInfo);
            if(SessionType==1000)return (String) Table_Session_Field.InTroopUin().get(HookEnv.SessionInfo);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    public static String getFriendUin(){
        try {
            int SessionType = (int)Table_Session_Field.isTroop().get(HookEnv.SessionInfo);
            if(SessionType==0)return (String) Table_Session_Field.friendUin().get(HookEnv.SessionInfo);
            if(SessionType==1)return "";
            if(SessionType==1000)return (String) Table_Session_Field.friendUin().get(HookEnv.SessionInfo);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    public static int getSessionID()
    {
        try {
            return (int) Table_Session_Field.isTroop().get(HookEnv.SessionInfo);
        } catch (Exception e) {
            return -1;
        }
    }
    public static String getGuildID(){
        try {
            int SessionType = (int)Table_Session_Field.isTroop().get(HookEnv.SessionInfo);
            if(SessionType==10014)return (String) Table_Session_Field.GuildID().get(HookEnv.SessionInfo);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    public static String getChannelID(){
        try {
            int SessionType = (int)Table_Session_Field.isTroop().get(HookEnv.SessionInfo);
            if(SessionType==10014)return (String) Table_Session_Field.ChannelID().get(HookEnv.SessionInfo);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    private static Object Build_SessionInfo_Guild(String GuildID,String ChannelID,boolean IsDirectMsg){
        try{
            if(IsDirectMsg){
                Object mObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"));
                Table_Session_Field.isTroop().set(mObj,10014);
                Table_Session_Field.GuildID().set(mObj,GuildID);
                Table_Session_Field.ChannelID().set(mObj,ChannelID);
                Table_Session_Field.CodeName().set(mObj,ChannelID);
                Bundle bundle = MField.GetField(mObj,"J",Bundle.class);
                bundle.putInt("guild_direct_message_flag",1);
                return mObj;
            }else {
                Object mObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"));
                Table_Session_Field.isTroop().set(mObj,10014);
                Table_Session_Field.GuildID().set(mObj,GuildID);
                Table_Session_Field.ChannelID().set(mObj,ChannelID);
                return mObj;
            }
        }catch (Exception e){
            LogUtils.error(TAG,"Can't not build guild session:\n"+e);
            return null;
        }

    }
    @HookItem(isDelayInit = false,isRunInAllProc = false)
    public static class Table_Session_Field extends BaseHookItem {
        private static Class<?> SessionInfo(){
            return MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo");
        }
        private static Field isTroop(){
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(SessionInfo(),"a", int.class) :
                    MField.FindField(SessionInfo(),"a",int.class);
            if (f != null) f.setAccessible(true);
            return f;
        }
        private static Field friendUin(){
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(SessionInfo(),"a", String.class) :
                    MField.FindField(SessionInfo(),"b",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }
        private static Field TroopCode(){
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(SessionInfo(),"b", String.class) :
                    MField.FindField(SessionInfo(),"c",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }
        private static Field InTroopUin(){
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(SessionInfo(),"c", String.class) :
                    MField.FindField(SessionInfo(),"d",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }
        private static Field GuildID(){
            return TroopCode();
        }
        private static Field ChannelID(){
            return friendUin();
        }
        private static Field CodeName(){
            Field f = MField.FindField(SessionInfo(),"e",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }

        private StringBuilder checkResult = new StringBuilder();
        @Override
        public String getTag() {
            return "Session_Field_Table";
        }

        @Override
        public boolean startHook() {
            return false;
        }

        @Override
        public boolean isEnable() {
            return false;
        }

        @Override
        public String getErrorInfo() {
            return checkResult.toString();
        }

        @Override
        public boolean check() {
            checkResult = new StringBuilder();
            if (isTroop()==null)checkResult.append("isTroop is null");
            if (friendUin()==null)checkResult.append("friendUin is null");
            if (TroopCode()==null)checkResult.append("TroopCode is null");
            if (InTroopUin()==null)checkResult.append("InTroopUin is null");
            if (CodeName()==null)checkResult.append("CodeName is null");
            return checkResult.length() == 0;
        }
    }
}
