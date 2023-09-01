package org.flitter.app.currency.dog.dao;

import org.flitter.app.currency.dog.model.BuyDog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyDogRepository extends CrudRepository<BuyDog, Long> {

    @Query("select a from BuyDog a where a.xuuid=:xuuid")
    Optional<BuyDog> findBuyId(@Param("xuuid") String xuuid);

    @Query("select a from BuyDog a where a.iscloseexternal=:iscloseexternal")
    List<BuyDog> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);

    @Query("select a from BuyDog a where a.isclose=:isclose AND a.maxprice<:maxprice")
    List<BuyDog> findBuyMaxPrice(@Param("isclose") Integer isclose, @Param("maxprice") Double maxprice);

    @Query("select a from BuyDog a where a.isclose=:isclose AND (a.tradeprice-:margin)>:price")
    List<BuyDog> findMarginPrice(@Param("isclose") Integer isclose, @Param("price") Double price, @Param("margin") Double margin);

}
