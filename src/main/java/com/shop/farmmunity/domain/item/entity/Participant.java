package com.shop.farmmunity.domain.item.entity;

import com.shop.farmmunity.base.baseEntity.BaseEntity;
import com.shop.farmmunity.domain.member.constant.Role;
import com.shop.farmmunity.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "participant")
@Getter
@Setter
@ToString
public class Participant extends BaseEntity {

    @Id
    @Column(name = "participant_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "group_id")
//    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean isHost;

    public static Participant createParticipant(Member member) {
        Participant participant = new Participant();
        participant.setMember(member);

        return participant;
    }
}
