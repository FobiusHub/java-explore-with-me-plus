package ru.practicum.ewm.service.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.request.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(long requesterId);

    boolean existsByRequesterIdAndEventId(long requesterId, long eventId);
}


