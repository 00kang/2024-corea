package corea.matching.service;

import config.ServiceTest;
import corea.exception.CoreaException;
import corea.fixture.MemberFixture;
import corea.fixture.RoomFixture;
import corea.matching.domain.PullRequestInfo;
import corea.matching.infrastructure.dto.GithubUserResponse;
import corea.matching.infrastructure.dto.PullRequestResponse;
import corea.matchresult.domain.MatchResult;
import corea.member.domain.Member;
import corea.member.domain.MemberRole;
import corea.member.repository.MemberRepository;
import corea.participation.domain.Participation;
import corea.participation.domain.ParticipationStatus;
import corea.participation.repository.ParticipationRepository;
import corea.room.domain.Room;
import corea.room.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static corea.exception.ExceptionType.ROOM_STATUS_INVALID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ServiceTest
class MatchingServiceTest {

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    private long roomId;
    private Member noPullRequestMember;

    @Test
    @DisplayName("PR 정보에 있는 멤버만 매칭이 된다.")
    void matched_with_pr_info_members() {
        PullRequestInfo pullRequestInfo = initialize();
        List<MatchResult> matchResults = matchingService.match(roomId, pullRequestInfo);
        boolean isExisted = matchResults.stream()
                .anyMatch(matchResult -> noPullRequestMember.equals(matchResult.getReviewee()) && noPullRequestMember.equals(matchResult.getReviewer()));

        assertThat(isExisted).isFalse();
    }

    private PullRequestInfo initialize() {
        Room room = roomRepository.save(RoomFixture.ROOM_DOMAIN(memberRepository.save(MemberFixture.MEMBER_ROOM_MANAGER_JOYSON()), LocalDateTime.now().plusHours(1)));
        roomId = room.getId();
        Member pororo = memberRepository.save(MemberFixture.MEMBER_PORORO());
        Member ash = memberRepository.save(MemberFixture.MEMBER_ASH());
        Member joysun = memberRepository.save(MemberFixture.MEMBER_YOUNGSU());
        Member movin = memberRepository.save(MemberFixture.MEMBER_MOVIN());
        Member ten = memberRepository.save(MemberFixture.MEMBER_TENTEN());
        Member cho = memberRepository.save(MemberFixture.MEMBER_CHOCO());
        noPullRequestMember = cho;

        participationRepository.save(new Participation(room, pororo, MemberRole.BOTH, room.getMatchingSize()));
        participationRepository.save(new Participation(room, ash, MemberRole.BOTH, room.getMatchingSize()));
        participationRepository.save(new Participation(room, joysun, MemberRole.BOTH, room.getMatchingSize()));
        participationRepository.save(new Participation(room, movin, MemberRole.BOTH, room.getMatchingSize()));
        participationRepository.save(new Participation(room, ten, MemberRole.BOTH, room.getMatchingSize()));
        participationRepository.save(new Participation(room, cho, MemberRole.BOTH, room.getMatchingSize()));

        return new PullRequestInfo(
                Map.of(
                        pororo.getGithubUserId(),
                        new PullRequestResponse("link", new GithubUserResponse(pororo.getGithubUserId()),
                                LocalDateTime.of(2024, 10, 12, 18, 00)),
                        ash.getGithubUserId(),
                        new PullRequestResponse("link", new GithubUserResponse(ash.getGithubUserId()),
                                LocalDateTime.of(2024, 10, 12, 18, 20)),
                        joysun.getGithubUserId(),
                        new PullRequestResponse("link", new GithubUserResponse(joysun.getGithubUserId()),
                                LocalDateTime.of(2024, 10, 12, 18, 30)),
                        movin.getGithubUserId(),
                        new PullRequestResponse("link", new GithubUserResponse(movin.getGithubUserId()),
                                LocalDateTime.of(2024, 10, 12, 18, 10)),
                        ten.getGithubUserId(),
                        new PullRequestResponse("link", new GithubUserResponse(ten.getGithubUserId()),
                                LocalDateTime.of(2024, 10, 12, 18, 01)
                        )
                ));
    }

    @Test
    @DisplayName("PR을 제출하지 않아 매칭에 실패하면 참여 상태를 PULL_REQUEST_NOT_SUBMITTED로 변경한다")
    void findOne_withPullRequestSubmission() {
        PullRequestInfo pullRequestInfo = initialize();
        matchingService.match(roomId, pullRequestInfo);

        Participation participation = participationRepository.findByRoomIdAndMemberId(roomId, noPullRequestMember.getId()).get();
        ParticipationStatus status = participation.getStatus();

        assertThat(status).isEqualTo(ParticipationStatus.PULL_REQUEST_NOT_SUBMITTED);
    }

    @Test
    @DisplayName("열린 방이 아닌 경우 매칭을 수행하면 예외가 발생한다.")
    void match_with_not_opened_room() {
        Room room = roomRepository.save(RoomFixture.ROOM_DOMAIN_WITH_CLOSED(memberRepository.save(MemberFixture.MEMBER_ROOM_MANAGER_JOYSON())));

        assertThatThrownBy(() -> matchingService.match(room.getId(), new PullRequestInfo(new HashMap<>())))
                .isInstanceOf(CoreaException.class)
                .satisfies(exception -> {
                    CoreaException coreaException = (CoreaException) exception;
                    assertThat(coreaException.getExceptionType()).isEqualTo(ROOM_STATUS_INVALID);
                });
    }
}
