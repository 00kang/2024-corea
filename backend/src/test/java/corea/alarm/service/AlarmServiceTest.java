package corea.alarm.service;

import config.ServiceTest;
import corea.alarm.domain.UserToUserAlarm;
import corea.alarm.domain.UserToUserAlarmRepository;
import corea.alarm.dto.AlarmCountResponse;
import corea.alarm.dto.AlarmResponse;
import corea.alarm.dto.AlarmResponses;
import corea.fixture.AlarmFixture;
import corea.fixture.MemberFixture;
import corea.fixture.RoomFixture;
import corea.member.domain.Member;
import corea.member.repository.MemberRepository;
import corea.room.domain.Room;
import corea.room.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ServiceTest
class AlarmServiceTest {

    @Autowired
    AlarmService alarmService;

    @Autowired
    UserToUserAlarmRepository userToUserAlarmRepository;

    @Autowired
    MemberRepository memberRepository;

    private Member actor;
    private Member receiver;
    private Member notReceiver;
    private Room interaction;
    private long interactionId;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        this.actor = memberRepository.save(MemberFixture.MEMBER_MOVIN());
        this.receiver = memberRepository.save(MemberFixture.MEMBER_PORORO());
        this.notReceiver = memberRepository.save(MemberFixture.MEMBER_ASH());
        this.interaction = roomRepository.save(RoomFixture.ROOM_DOMAIN(memberRepository.save(MemberFixture.MEMBER_ROOM_MANAGER_JOYSON())));
        interactionId = interaction.getId();
    }

    @Test
    @DisplayName("자신에게 작성된 알람들을 가져온다.")
    void get_alarm_count() {
        userToUserAlarmRepository.save(AlarmFixture.REVIEW_COMPLETE(actor.getId(), receiver.getId(), interactionId));
        userToUserAlarmRepository.save(AlarmFixture.REVIEW_COMPLETE(actor.getId(), notReceiver.getId(), interactionId));
        AlarmCountResponse response = alarmService.getUnReadAlarmCount(receiver.getId());
        assertThat(response.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("읽지 않은 알람만 가져온다.")
    void get_only_not_read_alarm_count() {
        userToUserAlarmRepository.save(AlarmFixture.REVIEW_COMPLETE(actor.getId(), receiver.getId(), interactionId));
        userToUserAlarmRepository.save(AlarmFixture.READ_REVIEW_COMPLETE(actor.getId(), receiver.getId(), interactionId));
        AlarmCountResponse response = alarmService.getUnReadAlarmCount(receiver.getId());
        assertThat(response.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자의 모든 알람을 최신순으로 가져온다.")
    void get_alarm() {
        UserToUserAlarm alarm1 = userToUserAlarmRepository.save(AlarmFixture.REVIEW_COMPLETE(actor.getId(), receiver.getId(), interactionId));
        UserToUserAlarm alarm2 = userToUserAlarmRepository.save(AlarmFixture.READ_REVIEW_COMPLETE(actor.getId(), receiver.getId(), interactionId));
        AlarmResponses responses = alarmService.getAlarm(receiver.getId());
        assertThat(responses.data()).hasSize(2)
                .usingElementComparatorIgnoringFields("createAt")
                .containsExactly(
                        AlarmResponse.from(alarm2, actor, interaction),
                        AlarmResponse.from(alarm1, actor,interaction)
                );
    }
}
