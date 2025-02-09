package uk.gov.justice.laa.crime.dces.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.model.EventTypeEntity;
import uk.gov.justice.laa.crime.dces.report.repository.EventTypeRepository;

@RequiredArgsConstructor
@Service
public class EventTypeService {

  private final EventTypeRepository eventTypeRepository;

  public EventTypeEntity getEventTypeEntity(Integer eventTypeId) {
    return eventTypeRepository.findById(eventTypeId).orElse(null);
  }
}