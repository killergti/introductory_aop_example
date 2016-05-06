package com.devexperts.service;

import com.devexperts.domain.Person;
import com.devexperts.tx.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author ifedorenkov
 */
public class PersonService extends CrudServiceBase<Person, Long> {

    /**
     * Generate a number of {@link Person} objects.
     */
    private final List<Person> personList = LongStream.rangeClosed(1, 10)
        .mapToObj(i -> new Person(i, "P" + i))
        .collect(Collectors.toList());


    @Override
    public Iterable<Person> findAll() {
        if (isDebugEnabled())
            debug("PersonService#findAll");
        Transaction tx = getTxManager().getTransaction();
        try {
            tx.begin();
            List<Person> result = new ArrayList<>(personList);
            tx.commit();
            if (isDebugEnabled())
                debug("PersonService#findAll=" + result);
            return result;
        } catch (Throwable t) {
            tx.rollback();
            error("PersonService#findAll=failed");
            throw t;
        }
    }

    @Override
    public Optional<Person> findOne(Long id) {
        if (isDebugEnabled())
            debug("PersonService#findOne[" + id + "]");
        Transaction tx = getTxManager().getTransaction();
        try {
            tx.begin();
            Optional<Person> result = personList.stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst();
            tx.commit();
            if (isDebugEnabled())
                debug("PersonService#findOne=" + result);
            return result;
        } catch (Throwable t) {
            tx.rollback();
            error("PersonService#findOne=failed");
            throw t;
        }
    }

    @Override
    public Person save(Person person) {
        if (isInfoEnabled())
            info("PersonService#save[" + person + "]");
        Transaction tx = getTxManager().getTransaction();
        try {
            tx.begin();
            Person result;
            if (person.getId() != 0) {
                Optional<Person> existing = findOne(person.getId());
                if (!existing.isPresent())
                    throw new IllegalArgumentException("Can not update a person. Entity not found.");
                result = existing.get();
                result.setName(person.getName());
            } else {
                long lastId = personList.stream()
                    .mapToLong(Person::getId)
                    .max()
                    .orElse(0L);
                person.setId(lastId + 1);
                personList.add(person);
                result = person;
            }
            tx.commit();
            if (isInfoEnabled())
                info("PersonService#save=" + result);
            return result;
        } catch (Throwable t) {
            tx.rollback();
            error("PersonService#save=failed");
            throw t;
        }
    }

    @Override
    public void remove(Person person) {
        if (isInfoEnabled())
            info("PersonService#remove[" + person + "]");
        Transaction tx = getTxManager().getTransaction();
        try {
            tx.begin();
            Optional<Person> existing = findOne(person.getId());
            if (!existing.isPresent())
                throw new IllegalArgumentException("Can not remove a person. Entity not found.");
            personList.remove(existing.get());
            tx.commit();
            if (isInfoEnabled())
                info("PersonService#remove=ok");
        } catch (Throwable t) {
            tx.rollback();
            error("PersonService#remove=failed");
            throw t;
        }
    }

}