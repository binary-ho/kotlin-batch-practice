package com.binaryho.kotlinbatchpractice.support

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@SpringBatchTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ExtendWith(SpringExtension::class)
abstract class BatchTestSupport {
    @Autowired
    protected lateinit var entityManagerFactory: EntityManagerFactory

    protected val entityManager: EntityManager by lazy { entityManagerFactory.createEntityManager() }

    protected fun <T> save(entity: T): T {
        entityManager.transaction.let { transaction ->
            transaction.begin()
            entityManager.merge(entity)
            transaction.commit()
            entityManager.clear()
        }
        return entity
    }

    protected fun <T> saveAll(entities: List<T>): List<T> {
        val entityManager = entityManagerFactory.createEntityManager()
        entityManager.transaction.let { transaction ->
            transaction.begin()
            for (entity in entities) {
                entityManager.merge(entity)
            }
            transaction.commit()
            entityManager.clear()
        }
        return entities
    }

    protected fun <T> deleteAll(path: Class<T>) {
        entityManager.transaction.let { transaction ->
            transaction.begin()

            val criteriaBuilder = entityManager.criteriaBuilder
            val criteriaDelete = criteriaBuilder.createCriteriaDelete(path)
            criteriaDelete.from(path)

            // Execute the delete operation
            entityManager.createQuery(criteriaDelete).executeUpdate()

            transaction.commit()
        }
    }
}
