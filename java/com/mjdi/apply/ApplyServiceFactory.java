package com.mjdi.apply;

import com.mjdi.util.Action;

public class ApplyServiceFactory {
    
    // 싱글톤 패턴
    private ApplyServiceFactory() {}
    private static ApplyServiceFactory instance = new ApplyServiceFactory();
    public static ApplyServiceFactory getInstance() { return instance; }

    public Action getAction(String cmd) {
        Action action = null;

        // [PART 1] 신규 단어 등록 관련
        if (cmd.equals("/request.apply")) {
            action = new ApplyRequestService();
        } else if (cmd.equals("/adminList.apply")) {
            action = new ApplyListService();
        } else if (cmd.equals("/approve.apply")) {
            action = new ApplyApproveService();
        } else if (cmd.equals("/reject.apply")) {
            action = new ApplyRejectService();
        }
        
        // [PART 2] 단어 수정 관련
        else if (cmd.equals("/requestEdit.apply")) {
            action = new ApplyEditRequestService();
        } else if (cmd.equals("/adminEditList.apply")) {
            action = new ApplyEditListService();
        } else if (cmd.equals("/approveEdit.apply")) {
            action = new ApplyEditApproveService();
        } else if (cmd.equals("/rejectEdit.apply")) {
            action = new ApplyEditRejectService();
        }
        
        // 어드민
        else if (cmd.equals("/adminMain.apply")) {
            action = new AdminMainService();
        }

        return action;
    }
}