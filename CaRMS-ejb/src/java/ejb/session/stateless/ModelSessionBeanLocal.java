/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Model;
import java.util.List;
import javax.ejb.Local;
import util.exception.CategoryNotFoundException;
import util.exception.ModelNotFoundException;

/**
 *
 * @author lhern
 */
@Local
public interface ModelSessionBeanLocal {

    public Long createNewModel(Model model, String categoryName) throws CategoryNotFoundException;

    public List<Model> viewAllModels();

    public Model retrieveModelById(Long modelId) throws ModelNotFoundException;

    public void updateModel(Model model) throws ModelNotFoundException;

    public void deleteModel(Long modelId) throws ModelNotFoundException;

    public Model retrieveModelByModelNameAndMake(String modelName, String makeName) throws ModelNotFoundException;
    
}
