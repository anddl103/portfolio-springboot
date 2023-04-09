package com.hybe.larva.entity.album;

import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.enums.OperationType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@ToString
@Data
@Builder
public class AlbumCard {

    private String id;

    private int position;

    private List<String> tags;

    private List<String> members;

    private CardContents contents;

    private OperationType operationType;

    private LocalDateTime createdAt;

}
