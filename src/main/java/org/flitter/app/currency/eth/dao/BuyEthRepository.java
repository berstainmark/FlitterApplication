package org.flitter.app.currency.eth.dao;

import org.flitter.app.currency.eth.model.BuyEth;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyEthRepository extends CrudRepository<BuyEth, Long> {

    @Query("select a from BuyEth a where a.xuuid=:xuuid")
    Optional<BuyEth> findBuyId(@Param("xuuid") String xuuid);

    @Query("select a from BuyEth a where a.iscloseexternal=:iscloseexternal")
    List<BuyEth> findByCloseExternal(@Param("iscloseexternal") Integer iscloseexternal);

    @Query("select a from BuyEth a where a.isclose=:isclose AND a.maxprice<:maxprice")
    List<BuyEth> findBuyMaxPrice(@Param("isclose") Integer isclose, @Param("maxprice") Double maxprice);

    @Query("select a from BuyEth a where a.isclose=:isclose AND (a.tradeprice-:margin)>:price")
    List<BuyEth> findMarginPrice(@Param("isclose") Integer isclose, @Param("price") Double price, @Param("margin") Double margin);


}
