package com.ctx.course_service.service.contract;

import java.util.UUID;

public interface ModuleCompletionService {
    void markModuleAsComplete(UUID studentId, UUID moduleId);
}
