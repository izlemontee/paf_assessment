package vttp2023.batch4.paf.assessment.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	private final String COLLECTION = "listings";

	// db.listings.distinct("address.suburb",
	// {
	// 	"address.suburb":{$nin :["",null]},
	// 	"address.country":{$regex:<country>,$options:"i"}
	// }
	// )
	public List<String> getSuburbs(String country) {
		List<String> nin = new ArrayList<>();
		nin.add("");
		nin.add(null);

		Query query = Query.query(Criteria.where("address.country").regex(country,"i")
									.and("address.suburb").nin(nin));
		List<String> result = template.findDistinct(query,"address.suburb","listings",Document.class,String.class);

		return result;
	}

		// db.listings.find(
		// 	{
		// 		"address.suburb":{$regex:"<suburb>",$options:"i"},
		// 		accommodates:{$gte:<persons>},
		// 		"min_nights":{$lte:<duration>},
		// 		price:{$lte:<price>}
		// 	},
		// 	{
		// 		"name":1,
		// 		"price":1,
		// 		"accommodates":1
		// 	}
		// 	)
		// .sort(
		// {
		//     "price":-1
		// }
		// )
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		Criteria criteria = Criteria.where("address.suburb").regex(suburb,"i")
							.and("accommodates").gte(persons)
							.and("min_nights").lte(duration)
							.and("price").lte(priceRange);
		Query query = Query.query(criteria).with(Sort.by(Direction.DESC, "price"));
		List<Document> docs = template.find(query,Document.class,COLLECTION);
		List<AccommodationSummary> accoms = new ArrayList<>();
		for(Document d:docs){
			String id = d.getString("_id"); // _id
			String name = d.getString("name"); // name
			int accomodates  = d.getInteger("accommodates"); // accommodates
			float price = d.get("price",Number.class).floatValue(); // price
			AccommodationSummary accom = new AccommodationSummary();
			accom.setAccomodates(accomodates);
			accom.setId(id);
			accom.setName(name);
			accom.setPrice(price);
			accoms.add(accom);
		}

		return accoms;
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
