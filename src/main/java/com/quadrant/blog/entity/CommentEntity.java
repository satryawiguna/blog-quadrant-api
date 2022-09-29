package com.quadrant.blog.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Builder
@Table(name = "qb8669_comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity extends BaseEntity<String> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(length = 100, nullable = false)
    private String comment;

    @ManyToOne
    private BlogEntity blog;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;
}
