package com.quadrant.blog.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Builder
@Table(name = "qb8669_blogs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE qb8669_blogs SET deleted_at = CURRENT_TIMESTAMP() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class BlogEntity extends BaseEntity<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 50, unique = true, nullable = false)
    private String slug;

    @Column(length = 2550)
    private String description;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "image_absolute")
    private String imageAbsolute;

    @Column(name = "image_file")
    private String imageFile;

    @Column(name = "image_size")
    private Long imageSize;

    private int viewed = 0;

    private Timestamp deletedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    private CategoryEntity category;

    @ManyToOne(cascade = CascadeType.ALL)
    private UserEntity user;
}
