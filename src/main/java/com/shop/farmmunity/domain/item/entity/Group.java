//package com.shop.farmmunity.domain.item.entity;
//
//import com.shop.farmmunity.base.baseEntity.BaseEntity;
//import com.shop.farmmunity.domain.member.entity.Member;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "groups")
//@Getter
//@Setter
//@ToString
//public class Group extends BaseEntity {
//
//    @Id
//    @Column(name = "group_id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="group_buying_id")
//    private GroupBuying groupBuying;
//
////    @ManyToOne(fetch = FetchType.LAZY)
////    @JoinColumn(name = "member_id")
////    private Member member;
//
//    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL,
//            orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Participant> participants = new ArrayList<>();
//
//    public void addParticipant(Participant participant) {
//
//        participants.add(participant);
//        participant.setGroup(this);
//    }
//}
