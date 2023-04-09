package com.hybe.larva.controller.client_api;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.dto.faq.UserFaqResponse;
import com.hybe.larva.dto.faq_category.FaqCategorySearchRequest;
import com.hybe.larva.dto.faq_category.UserFaqCategoryResponse;
import com.hybe.larva.dto.policy.UserPolicyResponse;
import com.hybe.larva.dto.question.UserQuestionResponse;
import com.hybe.larva.enums.Usage;
import com.hybe.larva.service.*;
import com.hybe.larva.util.CacheUtil;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/view")
@Controller
public class ViewController {

    private final PolicyService policyService;
    private final FaqService faqService;
    private final FaqCategoryService faqCategoryService;
    private final NoticeService noticeService;
    private final CacheUtil cacheUtil;
    private final QuestionService questionService;

    @RequestMapping("/policy/{policy}")
    public String privacyPolicy(
            @ApiParam(value = "이용 약관 정책")
            @PathVariable String policy,
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {

        UserPolicyResponse data = policyService.getPolicyUsage(Usage.valueOf(policy), lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", data.getSubject());
        model.addAttribute("detail", data);

        return "policy/detail";
    }

    @RequestMapping("/policy/termsOfService")
    public String termsOfService(
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
        UserPolicyResponse data = policyService.getPolicyUsage(Usage.TERMS_OF_SERVICE, lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", data.getSubject());
        model.addAttribute("detail", data);

        return "policy/detail";
    }

    @RequestMapping("/policy/privacyPolicy")
    public String privacyPolicy(
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
        UserPolicyResponse data = policyService.getPolicyUsage(Usage.TERMS_OF_SERVICE, lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", data.getSubject());
        model.addAttribute("detail", data);

        return "policy/detail";
    }

    @RequestMapping("/policy/push")
    public String push(
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
        UserPolicyResponse data = policyService.getPolicyUsage(Usage.PUSH, lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", data.getSubject());
        model.addAttribute("detail", data);

        return "policy/detail";
    }

    @RequestMapping("/policy/serviceOperationPolicy")
    public String serviceOperationPolicy(
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
        UserPolicyResponse data = policyService.getPolicyUsage(Usage.SERVICE_OPERATION_POLICY, lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", data.getSubject());
        model.addAttribute("detail", data);

        return "policy/detail";
    }

    @RequestMapping("/policy/openSourceLicense")
    public String openSourceLicense(
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
        UserPolicyResponse data = policyService.getPolicyUsage(Usage.OPEN_SOURCE_LICENSE, lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", data.getSubject());
        model.addAttribute("detail", data);

        return "policy/detail";
    }


    @RequestMapping("/notice")
    public String searchNotice(
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
        model.addAttribute("lang", lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", "Notice");
        return "notice/list";
    }

    @RequestMapping("/serviceCenter")
    public String serviceCenter(
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
        FaqCategorySearchRequest request = FaqCategorySearchRequest.builder()
                .offset(0).limit(-1)
                .build();
        Page<UserFaqCategoryResponse> data = faqCategoryService.searchFaqCategoryForUser(request, lang);

        List<UserFaqCategoryResponse> list = data.getContent();
        model.addAttribute("lang", lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", "Service Center");
        model.addAttribute("category", list);
        return "service_center/serviceCenter";
    }

    @RequestMapping("/faq/{category}")
    public String searchFaqCategory(
            @ApiParam(value = "카테고리")
            @PathVariable String category,
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
        UserFaqCategoryResponse response = faqCategoryService.getFaqCategoryForUser(category, lang);
        model.addAttribute("lang", lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", response.getSubject());
        model.addAttribute("category", category);
        return "faq/list";
    }

    @RequestMapping("/faq/detail/{id}")
    public String searchFaqDetail(
            @ApiParam(value = "이용 약관 정책")
            @PathVariable String id,
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
        UserFaqResponse data = faqService.getFaqForUser(id, lang);
        model.addAttribute("lang", lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", data.getSubject());
        model.addAttribute("detail", data);
        return "faq/detail";
    }

    @RequestMapping("/question")
    public String searchQuestion(
            @ApiParam(value = "언어코드")
            @RequestParam(required = false)  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
        model.addAttribute("lang", lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", "Question");
        return "question/list";
    }

    @RequestMapping("/question/add")
    public String addQuestion(
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
        model.addAttribute("lang", lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", "Question Register");
        return "question/add";
    }

    @RequestMapping("/question/detail/{id}")
    public String searchQuestionDetail(
            @ApiParam(value = "이용 약관 정책")
            @PathVariable String id,
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            @ApiParam(value = "from")
            @RequestParam(required = false)  String from,
            Model model
    ) {
//        String uid = CommonUser.getUid();
//        UserQuestionResponse data = questionService.getQuestionForUser(id, uid);
        model.addAttribute("lang", lang);
        model.addAttribute("from", emptyFromCheck(from));
        model.addAttribute("title", "Question contents");
        model.addAttribute("id", id);
        return "question/detail";
    }




    @RequestMapping("/sample")
    public String searchFaqDetail(
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            Model model
    ) {
        return "sample";
    }

    @RequestMapping("/temp")
    public String searchtemp(
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang,
            Model model
    ) {
        model.addAttribute("title", "temp");
        return "temp";
    }

    private String emptyFromCheck(String from) {
        if(from == null || from.equals("") || from.trim().equals("")) {
            return "Web";
        }
        return from;
    }

}
