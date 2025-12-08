package com.mjdi.word;

import com.mjdi.util.Action;

public class WordServiceFactory {
    
    // 싱글톤 패턴
    private WordServiceFactory() {}
    private static WordServiceFactory instance = new WordServiceFactory();
    public static WordServiceFactory getInstance() { return instance; }

    public Action getAction(String cmd) {
        Action action = null;

        if (cmd == null || cmd.equals("main")) {
            action = new WordMainService();
        } else if (cmd.equals("word_search")) {
            action = new WordSearchService();
        } else if (cmd.equals("word_view")) {
            action = new WordViewService();
        } else if (cmd.equals("auto_complete")) { 
            action = new WordAutoCompleteService();
        } else if (cmd.equals("word_req")) {
            action = new WordReqService();
        } else if (cmd.equals("use_point")) {
            action = new PointUseService();
        }else if (cmd.equals("bookmark_toggle")) {
            action = new BookmarkToggleService();
        } else if (cmd.equals("bookmark_list")) {
            action = new BookmarkListService();
        }
        
        return action;
    }
}