//package com.example.blog.controller.ui.category;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.example.blog.model.Categories;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CategoriesViewModel extends ViewModel {
//
//    List<Categories> catList = new ArrayList<>();
//    private MutableLiveData<List<Categories>> catData;
//
//    public CategoriesViewModel() {
////        mText = new MutableLiveData<>();
////        mText.setValue("This is tools fragment");
//
//        Categories cat=new Categories(2,"test");
//
//
//
//
//        catList.add(cat);
//        for(int i=0;i<10;i++){
//            cat=new Categories(i,"test"+i);
//            catList.add(cat);
//        }
//
//        catData.setValue(catList);
//    }
//
//    public LiveData<List<Categories>> getCat() {
//        catData.postValue(catList);
//        return catData;
//    }
//}