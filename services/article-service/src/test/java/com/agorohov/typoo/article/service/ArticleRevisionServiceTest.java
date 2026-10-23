package com.agorohov.typoo.article.service;

import com.agorohov.shared.common.event.TypooEventPublisher;
import com.agorohov.typoo.article.repository.ArticleRevisionRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticleRevisionServiceTest {

    @Mock
    private ArticleRevisionRepository articleRevisionRepository;

    @Mock
    private TypooEventPublisher eventPublisher;

    @InjectMocks
    ArticleRevisionService subject;
}