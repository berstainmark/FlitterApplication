package org.flitter.app.currency.btc.dao;

import org.flitter.app.currency.btc.model.BuyBtc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyBtcRepository extends CrudRepository<BuyBtc, Long> {

    @Query("select a from BuyBtc a where a.xuuid=:xuuid")
    Optional<BuyBtc> findBuyId(@Param("xuuid") String xuuid);

    @Query("select a from BuyBtc a where a.iscloseexternal=:iscloseexternal")
    List<BuyBtc> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);

    @Query("select a from BuyBtc a where a.isclose=:isclose AND a.maxprice<:maxprice")
    List<BuyBtc> findBuyMaxPrice(@Param("isclose") Integer isclose, @Param("maxprice") Double maxprice);

    @Query("select a from BuyBtc a where a.isclose=:isclose AND (a.tradeprice-:margin)>:price")
    List<BuyBtc> findMarginPrice(@Param("isclose") Integer isclose, @Param("price") Double price, @Param("margin") Double margin);


}
