package com.merakianalytics.orianna.datapipeline.riotapi;

import java.util.Iterator;
import java.util.Map;

import com.merakianalytics.datapipelines.PipelineContext;
import com.merakianalytics.datapipelines.iterators.CloseableIterator;
import com.merakianalytics.datapipelines.iterators.CloseableIterators;
import com.merakianalytics.datapipelines.sources.Get;
import com.merakianalytics.datapipelines.sources.GetMany;
import com.merakianalytics.orianna.datapipeline.common.HTTPClient;
import com.merakianalytics.orianna.datapipeline.common.Utilities;
import com.merakianalytics.orianna.datapipeline.common.rates.RateLimiter;
import com.merakianalytics.orianna.datapipeline.riotapi.RiotAPI.Configuration;
import com.merakianalytics.orianna.types.common.Platform;
import com.merakianalytics.orianna.types.dto.championmastery.ChampionMasteries;
import com.merakianalytics.orianna.types.dto.championmastery.ChampionMastery;
import com.merakianalytics.orianna.types.dto.championmastery.ChampionMasteryScore;

public class ChampionMasteryAPI extends RiotAPIService {
    public ChampionMasteryAPI(final Configuration config, final HTTPClient client, final Map<Platform, RateLimiter> applicationRateLimiters,
        final Map<Platform, Object> applicationRateLimiterLocks) {
        super(config, client, applicationRateLimiters, applicationRateLimiterLocks);
    }

    @Get(ChampionMastery.class)
    public ChampionMastery getChampionMastery(final Map<String, Object> query, final PipelineContext context) {
        final Platform platform = (Platform)query.get("platform");
        final String summonerId = (String)query.get("summonerId");
        final Number championId = (Number)query.get("championId");
        Utilities.checkNotNull(platform, "platform", summonerId, "summonerId", championId, "championId");

        final String endpoint = "lol/champion-mastery/v4/champion-masteries/by-summoner/" + summonerId + "/by-champion/" + championId;
        final ChampionMastery data = get(ChampionMastery.class, endpoint, platform,
            "lol/champion-mastery/v4/champion-masteries/by-summoner/summonerId/by-champion/championId");
        if(data == null) {
            return null;
        }

        data.setPlatform(platform.getTag());
        return data;
    }

    @Get(ChampionMasteries.class)
    public ChampionMasteries getChampionMasteryList(final Map<String, Object> query, final PipelineContext context) {
        final Platform platform = (Platform)query.get("platform");
        final String summonerId = (String)query.get("summonerId");
        Utilities.checkNotNull(platform, "platform", summonerId, "summonerId");

        final String endpoint = "lol/champion-mastery/v4/champion-masteries/by-summoner/" + summonerId;
        final ChampionMasteries data = get(ChampionMasteries.class, endpoint, platform,
            "lol/champion-mastery/v4/champion-masteries/by-summoner/summonerId");
        if(data == null) {
            return null;
        }

        data.setSummonerId(summonerId);
        data.setPlatform(platform.getTag());
        for(final ChampionMastery mastery : data) {
            mastery.setPlatform(platform.getTag());
        }
        return data;
    }

    @Get(ChampionMasteryScore.class)
    public ChampionMasteryScore getChampionMasteryScore(final Map<String, Object> query, final PipelineContext context) {
        final Platform platform = (Platform)query.get("platform");
        final String summonerId = (String)query.get("summonerId");
        Utilities.checkNotNull(platform, "platform", summonerId, "summonerId");

        final String endpoint = "lol/champion-mastery/v4/scores/by-summoner/" + summonerId;
        final ChampionMasteryScore data = get(ChampionMasteryScore.class, endpoint, platform, "lol/champion-mastery/v4/scores/by-summoner/summonerId");
        if(data == null) {
            return null;
        }

        data.setPlatform(platform.getTag());
        data.setSummonerId(summonerId);
        return data;
    }

    @SuppressWarnings("unchecked")
    @GetMany(ChampionMastery.class)
    public CloseableIterator<ChampionMastery> getManyChampionMastery(final Map<String, Object> query, final PipelineContext context) {
        final Platform platform = (Platform)query.get("platform");
        final String summonerId = (String)query.get("summonerId");
        final Iterable<Number> championIds = (Iterable<Number>)query.get("championIds");
        Utilities.checkNotNull(platform, "platform", summonerId, "summonerId", championIds, "championIds");

        final String endpoint = "lol/champion-mastery/v4/champion-masteries/by-summoner/" + summonerId;
        final ChampionMasteries data = get(ChampionMasteries.class, endpoint, platform,
            "lol/champion-mastery/v4/champion-masteries/by-summoner/summonerId");
        if(data == null) {
            return null;
        }

        data.setSummonerId(summonerId);
        data.setPlatform(platform.getTag());
        for(final ChampionMastery mastery : data) {
            mastery.setPlatform(platform.getTag());
        }

        final Iterator<Number> iterator = championIds.iterator();
        return CloseableIterators.from(new Iterator<ChampionMastery>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public ChampionMastery next() {
                final Number championId = iterator.next();
                for(final ChampionMastery mastery : data) {
                    if(mastery.getChampionId() == championId.longValue()) {
                        return mastery;
                    }
                }
                return null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        });
    }

    @SuppressWarnings("unchecked")
    @GetMany(ChampionMasteries.class)
    public CloseableIterator<ChampionMasteries> getManyChampionMasteryList(final Map<String, Object> query, final PipelineContext context) {
        final Platform platform = (Platform)query.get("platform");
        final Iterable<String> summonerIds = (Iterable<String>)query.get("summonerIds");
        Utilities.checkNotNull(platform, "platform", summonerIds, "summonerIds");

        final Iterator<String> iterator = summonerIds.iterator();
        return CloseableIterators.from(new Iterator<ChampionMasteries>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public ChampionMasteries next() {
                final String summonerId = iterator.next();

                final String endpoint = "lol/champion-mastery/v4/champion-masteries/by-summoner/" + summonerId;
                final ChampionMasteries data = get(ChampionMasteries.class, endpoint, platform,
                    "lol/champion-mastery/v4/champion-masteries/by-summoner/summonerId");
                if(data == null) {
                    return null;
                }

                data.setSummonerId(summonerId);
                data.setPlatform(platform.getTag());
                for(final ChampionMastery mastery : data) {
                    mastery.setPlatform(platform.getTag());
                }
                return data;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        });
    }

    @SuppressWarnings("unchecked")
    @GetMany(ChampionMasteryScore.class)
    public CloseableIterator<ChampionMasteryScore> getManyChampionMasteryScore(final Map<String, Object> query, final PipelineContext context) {
        final Platform platform = (Platform)query.get("platform");
        final Iterable<String> summonerIds = (Iterable<String>)query.get("summonerIds");
        Utilities.checkNotNull(platform, "platform", summonerIds, "summonerIds");

        final Iterator<String> iterator = summonerIds.iterator();
        return CloseableIterators.from(new Iterator<ChampionMasteryScore>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public ChampionMasteryScore next() {
                final String summonerId = iterator.next();

                final String endpoint = "lol/champion-mastery/v4/scores/by-summoner/" + summonerId;
                final ChampionMasteryScore data = get(ChampionMasteryScore.class, endpoint, platform, "lol/champion-mastery/v4/scores/by-summoner/summonerId");
                if(data == null) {
                    return null;
                }

                data.setPlatform(platform.getTag());
                data.setSummonerId(summonerId);
                return data;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        });
    }
}
