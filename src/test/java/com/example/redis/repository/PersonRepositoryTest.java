package com.example.redis.repository;

import com.example.redis.AppConfig;
import com.example.redis.model.Address;
import com.example.redis.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class PersonRepositoryTest {

    @Autowired
    PersonRepository repo;

    @Test
    public void basicCrudOperations() {

        Address home = new Address("Korea", "Seoul");
        Person person = new Person(null, "chiman", "kim", home);

        // when
        Person savedPerson = repo.save(person);

        // then
        Optional<Person> findPerson = repo.findById(savedPerson.getId());

        assertThat(findPerson.isPresent()).isEqualTo(Boolean.TRUE);
        assertThat(findPerson.get().getFirstname()).isEqualTo(person.getFirstname());


    }

}