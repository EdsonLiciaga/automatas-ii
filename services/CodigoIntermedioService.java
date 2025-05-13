package services;

import java.util.List;
import java.util.stream.Collectors;

import models.Token;

public class CodigoIntermedioService extends BaseService
{
	public static List<Token> getTokensFromCondition(int linea, List<Token> tokens)
	{
		List<Token> tokensCondition = tokens
			.stream()
			.filter(t -> t.numlinea == linea)
			.collect(Collectors.toList());  
		tokensCondition.removeFirst(); 

		return tokensCondition; 
	}

	// public static List<Token> getTokensInStructure(int linea, List<Token> tokens)
	// {

	// 	List<Token> tokensStructure = tokens
	// 		.stream()
	// 		.filter(null)
	// }


}
