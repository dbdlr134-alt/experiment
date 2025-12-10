package com.mjdi.notice;

import com.mjdi.util.Action;

public class NoticeServiceFactory {

    // 싱글톤 패턴
    private NoticeServiceFactory() {}
    private static NoticeServiceFactory instance = new NoticeServiceFactory();
    public static NoticeServiceFactory getInstance() { 
    	return instance; 
    }

    public Action getAction(String cmd) {
        Action action = null;

        if(cmd == null || cmd.equals("notice_list")) {//공지목록
            action = new NoticeListService();
        }else if(cmd.equals("notice_view")) { // 상세보기
            action = new NoticeViewService();
        }else if(cmd.equals("notice_write")) { // 글 등록
            action = new NoticeWriteService();
        }else if(cmd.equals("notice_write_pro")) { // 글 등록처리
            action = new NoticeWriteProService();
        }else if(cmd.equals("notice_delete")) { // 글 삭제
            action = new NoticeDeleteService();
        }else if(cmd.equals("notice_modify")) { // 수정 화면
            action = new NoticeModifyService();
        } else if(cmd.equals("notice_modify_pro")) { // 수정 처리
            action = new NoticeModifyProService();
        }else if(cmd.equals("notice_top")) { // 공지 상단 고정/해제
            action = new NoticeTopService();
        }

        return action;
    }
}