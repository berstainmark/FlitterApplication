package org.flitter.app.currency.dog.dao;

import org.flitter.app.currency.dog.model.SellDog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellDogRepository extends CrudRepository<SellDog, Long> {

    @Query("select a from SellDog a where a.xuuid=:xuuid")
    Optional<SellDog> findSaleId(@Param("xuuid") String xuuid);

    @Query("select a from SellDog a where a.iscloseexternal=:iscloseexternal")
    List<SellDog> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);
}
