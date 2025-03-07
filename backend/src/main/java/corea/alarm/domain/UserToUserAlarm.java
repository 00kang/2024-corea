package corea.alarm.domain;

import corea.global.BaseTimeEntity;
import corea.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserToUserAlarm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlarmActionType alarmActionType;

    /**
     * C 라는 방에서 A 가 코드 리뷰를 B 에게 완료했다.
     * A 의 ID 가 actorId, B 의 ID 가 receiverId
     * C 의 ID 가 interactionId
     */
    private Long actorId;

    private Long receiverId;

    private Long interactionId;

    private boolean isRead;

    public UserToUserAlarm(AlarmActionType alarmActionType, long actorId, long receiverId, long interactionId, boolean isRead) {
        this(null, alarmActionType, actorId, receiverId, interactionId, isRead);
    }

    public boolean isStatus(boolean status) {
        return isRead == status;
    }

    public String getActionType() {
        return alarmActionType.name();
    }

    public boolean isNotReceiver(Member member) {
        return !receiverId.equals(member.getId());
    }

    public void read() {
        isRead = true;
    }

    public boolean isUrgeAlarm() {
        return alarmActionType == AlarmActionType.REVIEW_URGE;
    }
}
