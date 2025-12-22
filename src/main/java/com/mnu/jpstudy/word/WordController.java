package com.mnu.jpstudy.word;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/word")
public class WordController {

    private final WordService wordService;
    private final BookmarkService bookmarkService;
    private final WordRequestService wordRequestService;

    @GetMapping
    public String search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "user", required = false) String user,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("word").ascending());
        Page<JapaneseWord> wordPage = wordService.searchWords(query, pageable);

        model.addAttribute("wordPage", wordPage);
        model.addAttribute("query", query);
        model.addAttribute("user", user);
        model.addAttribute("requestForm", prepareRequestForm(user, null));
        model.addAttribute("pendingRequests", wordRequestService.getPendingRequests());

        return "search";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable("id") Long id,
            @RequestParam(value = "user", required = false) String user,
            @RequestParam(value = "from", required = false) String from,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            JapaneseWord word = wordService.findById(id);
            boolean isBookmarked = bookmarkService.isBookmarked(user, id);

            model.addAttribute("word", word);
            model.addAttribute("bookmarked", isBookmarked);
            model.addAttribute("user", user);
            model.addAttribute("from", from);
            model.addAttribute("requestForm", prepareRequestForm(user, word));

            return "result";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            return "redirect:/word";
        }
    }

    @GetMapping("/bookmarks")
    public String bookmarks(@RequestParam(value = "user", required = false) String user, Model model) {
        List<JapaneseWord> bookmarks = bookmarkService.getBookmarks(user);
        model.addAttribute("bookmarks", bookmarks);
        model.addAttribute("user", user);
        return "bookmarks";
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    public List<WordSummary> autoComplete(@RequestParam("key") String key) {
        return wordService.autoComplete(key).stream()
                .map(WordSummary::from)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/bookmark")
    public String toggleBookmark(
            @PathVariable("id") Long id,
            @RequestParam(value = "user", required = false) String user,
            RedirectAttributes redirectAttributes) {

        boolean nowBookmarked = bookmarkService.toggleBookmark(user, id);
        redirectAttributes.addFlashAttribute("msg", nowBookmarked ? "즐겨찾기에 추가되었습니다." : "즐겨찾기가 해제되었습니다.");
        return "redirect:/word/" + id + (user != null ? "?user=" + user : "");
    }

    @PostMapping("/requests/new")
    public String submitNewRequest(
            @Valid WordRequestForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "user", required = false) String user) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("msg", "신청 정보를 모두 입력해 주세요.");
            return "redirect:/word";
        }

        form.setRequestedBy(resolveUser(user));
        wordRequestService.submitNewRequest(form);
        redirectAttributes.addFlashAttribute("msg", "단어 신청이 접수되었습니다. 관리자 검토 후 반영됩니다.");
        return "redirect:/word";
    }

    @PostMapping("/requests/edit")
    public String submitEditRequest(
            @Valid WordRequestForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "user", required = false) String user) {

        if (bindingResult.hasErrors() || form.getWordId() == null) {
            redirectAttributes.addFlashAttribute("msg", "수정 신청 정보를 확인해 주세요.");
            return "redirect:/word";
        }

        form.setRequestedBy(resolveUser(user));
        wordRequestService.submitEditRequest(form);
        redirectAttributes.addFlashAttribute("msg", "수정 요청이 접수되었습니다.");
        return "redirect:/word/" + form.getWordId();
    }

    @GetMapping("/requests")
    public String requestList(Model model) {
        model.addAttribute("pendingRequests", wordRequestService.getPendingRequests());
        return "requests";
    }

    @PostMapping("/requests/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        wordRequestService.approve(id, "admin");
        redirectAttributes.addFlashAttribute("msg", "신청을 승인했습니다.");
        return "redirect:/word/requests";
    }

    @PostMapping("/requests/{id}/reject")
    public String reject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        wordRequestService.reject(id, "admin");
        redirectAttributes.addFlashAttribute("msg", "신청을 반려했습니다.");
        return "redirect:/word/requests";
    }

    private WordRequestForm prepareRequestForm(String user, JapaneseWord word) {
        WordRequestForm form = new WordRequestForm();
        form.setRequestedBy(resolveUser(user));
        if (word != null) {
            form.setWordId(word.getWordId());
            form.setWord(word.getWord());
            form.setDoc(word.getDoc());
            form.setKorean(word.getKorean());
            form.setJlpt(word.getJlpt());
        }
        return form;
    }

    private String resolveUser(String user) {
        return (user == null || user.isBlank()) ? "guest" : user;
    }

    public record WordSummary(Long id, String word, String korean) {
        public static WordSummary from(JapaneseWord entity) {
            return new WordSummary(entity.getWordId(), entity.getWord(), entity.getKorean());
        }
    }
}