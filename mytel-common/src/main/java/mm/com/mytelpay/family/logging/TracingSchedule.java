package mm.com.mytelpay.family.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class TracingSchedule {

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void tracingSchedule(ProceedingJoinPoint point) throws Throwable {
        MDC.put(RequestUtils.CONTEXT_REQUEST_ID, RequestUtils.generateRequestId());
        MDC.put("target", point.getSignature().getName());
        long start = System.currentTimeMillis();
        point.proceed();
    }
}
