package jjug;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jjug.conference.Conference;
import jjug.conference.ConferenceRepository;
import jjug.conference.enums.ConfStatus;
import jjug.submission.Submission;
import jjug.submission.SubmissionRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private final ConferenceRepository conferenceRepository;
	private final SubmissionRepository submissionRepository;
	private final CfpProps props;

	@GetMapping("/")
	Object home(@AuthenticationPrincipal CfpUser user, Model model,
			@PageableDefault(sort = "confDate", direction = Sort.Direction.DESC, size = 5) Pageable pageable) {
		Page<Conference> cfpConferences = conferenceRepository
				.findByConfStatus(ConfStatus.CFP, pageable);
		Page<Conference> votingConferences = conferenceRepository
				.findByConfStatus(ConfStatus.VOTE, pageable);
		Page<Conference> openConferences = conferenceRepository
				.findByConfStatus(ConfStatus.OPEN, pageable);
		List<Submission> submissions = submissionRepository
				.findBySpeaker_GithubOrderByConference_ConfDateDescSubmissionStatusAscCreatedAtAsc(
						user.getGithub());

		model.addAttribute("cfpConferences", cfpConferences);
		model.addAttribute("votingConferences", votingConferences);
		model.addAttribute("openConferences", openConferences);
		model.addAttribute("submissions", submissions);
		if (props.getAdminUsers().contains(user.getGithub())) {
			Page<Conference> conferences = conferenceRepository.findAll(pageable);
			model.addAttribute("conferences", conferences);
		}
		return "index";
	}
}
