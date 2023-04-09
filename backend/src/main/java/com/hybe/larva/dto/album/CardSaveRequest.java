package com.hybe.larva.dto.album;

import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.enums.OperationType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ToString
@Getter
public class CardSaveRequest {

    private String id;

    private int position;

    private List<String> tags;

    private List<String> members;

    private CardContents contents;

    private OperationType operationType;

}
