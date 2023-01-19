package no.ding.pk.listener;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CleanUpH2DatabaseListener implements TestExecutionListener, Order {
    
    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        TestExecutionListener.super.beforeTestMethod(testContext);
        cleanUpDatabase(testContext);
    }
    
    private void cleanUpDatabase(TestContext testContext) {
        log.info("Cleaning up database begin");
        CleanUpH2DatabaseService cleanUpH2DatabaseService = testContext.getApplicationContext().getBean(CleanUpH2DatabaseService.class);
        cleanUpH2DatabaseService.cleanUp("PUBLIC");
        log.info("Cleaning up database finished");
    }

    
    
    @Override
    public Order reverse() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean isAscending() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public Expression<?> getExpression() {
        // TODO Auto-generated method stub
        return null;
    }
    
}