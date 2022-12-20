package org.josers.lifeadvice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LifeAdviceMessageService {
    private final Random randomSelector;
    private final List<String> lifeAdvices;

    public String getMessage() {
        return lifeAdvices.get(randomSelector.nextInt(lifeAdvices.size()));
    }
}
