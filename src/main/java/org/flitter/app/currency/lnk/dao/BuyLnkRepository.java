package org.flitter.app.currency.lnk.dao;

import org.flitter.app.currency.lnk.model.BuyLnk;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyLnkRepository extends CrudRepository<BuyLnk, Long> {

    @Query("select a from BuyLnk a where a.xuuid=:xuuid")
    Optional<BuyLnk> findBuyId(@Param("xuuid") String xuuid);

    @Query("select a from BuyLnk a where a.iscloseexternal=:iscloseexternal")
    List<BuyLnk> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);

    @Query("select a from BuyLnk a where a.isclose=:isclose AND a.maxprice<:maxprice")
    List<BuyLnk> findBuyMaxPrice(@Param("isclose") Integer isclose, @Param("maxprice") Double maxprice);

    @Query("select a from BuyLnk a where a.isclose=:isclose AND (a.tradeprice-:margin)>:price")
    List<BuyLnk> findMarginPrice(@Param("isclose") Integer isclose, @Param("price") Double price, @Param("margin") Double margin);


}
