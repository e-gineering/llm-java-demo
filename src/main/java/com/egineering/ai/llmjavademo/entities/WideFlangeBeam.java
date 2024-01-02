package com.egineering.ai.llmjavademo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "wide_flange_beams")
public class WideFlangeBeam {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "size")
    private String size;

    @Column(name = "weight_per_foot")
    private BigDecimal weightPerFoot;

    @Column(name = "section_depth")
    private BigDecimal sectionDepth;

    @Column(name = "flange_width")
    private BigDecimal flangeWidth;

    @Column(name = "flange_thickness")
    private BigDecimal flangeThickness;

    @Column(name = "web_thickness")
    private BigDecimal webThickness;

    @Column(name = "weight_per_forty_feet")
    private Integer weightPerFortyFeet;

    @Column(name = "weight_per_sixty_feet")
    private Integer weightPerSixtyFeet;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        WideFlangeBeam that = (WideFlangeBeam) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
