package com.somoim.model.entity;

import com.somoim.enumeration.GenderType;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class UserEntity {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    private String name;

    private String birth;

    private GenderType gender;

    @Column(name = "address_id")
    private Integer addressId;

    @Column(name = "region_id")
    private Integer regionId;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Setter
    @Column(name = "modify_at")
    private LocalDateTime modifyAt;

    @Setter
    private Boolean disband;

    @Builder
    public UserEntity(String email, String password, LocalDateTime createAt, LocalDateTime modifyAt,
            Boolean disband) {
        this.email = email;
        this.password = password;
        this.createAt = createAt;
        this.modifyAt = modifyAt;
        this.disband = disband;
    }
}
