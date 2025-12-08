package com.mjdi.user;

import com.mjdi.util.Action;

public class UserServiceFactory {
    
    // 싱글톤 패턴
    private UserServiceFactory() {}
    private static UserServiceFactory instance = new UserServiceFactory();
    public static UserServiceFactory getInstance() { return instance; }

    public Action getAction(String cmd) {
        Action action = null;

        if (cmd.equals("/sendSms.do")) {
            action = new SmsSendService();
        } else if (cmd.equals("/checkSms.do")) {
            action = new SmsCheckService();
        } else if (cmd.equals("/join.do")) {
            action = new UserJoinService();
        } else if (cmd.equals("/login.do")) {
            action = new UserLoginService();
        } else if (cmd.equals("/logout.do")) {
            action = new UserLogoutService();
        } else if (cmd.equals("/checkPass.do")) {
            action = new UserCheckPassService();
        } else if (cmd.equals("/updateAll.do")) {
            action = new UserUpdateService();
        } else if (cmd.equals("/findId.do")) {
            action = new UserFindIdService();
        } else if (cmd.equals("/findPw.do")) {
            action = new UserFindPwService();
        }else if (cmd.equals("/request_profile.do")) {
            action = new ProfileRequestService();
        }else if (cmd.equals("/profileReqList.do")) {
            action = new ProfileReqListService();
        } else if (cmd.equals("/approveProfileReq.do")) {
            action = new ProfileReqApproveService();
        } else if (cmd.equals("/rejectProfileReq.do")) {
            action = new ProfileReqRejectService();
        }else if (cmd.equals("/userList.do")) {
            action = new UserListService();
        }else if (cmd.equals("/msgSend.do")) {
            action = new MessageSendService();
        } else if (cmd.equals("/msgBox.do")) {
            action = new MessageBoxService();
        }else if (cmd.equals("/userBlock.do")) {
            action = new UserBlockService();
        }else if (cmd.equals("/themeBuy.do")) {
            action = new ThemeBuyService();
        }else if (cmd.equals("/themeApply.do")) {
            action = new ThemeApplyService();
        }


        return action;
    }
}