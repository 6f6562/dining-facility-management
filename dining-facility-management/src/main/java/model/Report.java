package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "period", nullable = false)
    private String period;

    @Column(name = "generated_date", nullable = false)
    private LocalDate generatedDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
} 