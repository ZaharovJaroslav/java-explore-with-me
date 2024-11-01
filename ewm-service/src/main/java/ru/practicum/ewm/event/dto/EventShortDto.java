package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

@Getter
@Setter
@Builder
public class EventShortDto {
   private String annotation;
   private CategoryDto category;
   private Long confirmedRequests;
   private String eventDate;
   private Long id;
   private UserShortDto initiator;
   private Boolean paid;
   private String title;
   private Long views;
}
