package org.codeworks.dsp.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.codeworks.dsp.model.dto.Materials;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.service.CreativesService;
import org.codeworks.dsp.service.RtbCallService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by benjaminkc on 16/10/26.
 */
@Aspect
public class MaterialForRtbInterceptor {

    @Autowired
    private RtbCallService rtbApiService;

    @Autowired
    private CreativesService creativesService;



    @Pointcut("execution(* org.codeworks.dsp.service.impl.BesApiServiceImpl.scheduleCheckAndUpdateCreatives(..))")
    private void scheduleCheckAndUpdateCreatives() {}
    @Around("scheduleCheckAndUpdateCreatives()")
    public List<Integer> doPostMaterialToRtbAtBaiduAudit(ProceedingJoinPoint pjp) throws Throwable{
        List<Integer> ids = (ArrayList)pjp.proceed(pjp.getArgs());
        sendAndCheckMaterial(ids);
        return ids;
    }

    @Pointcut("execution(* org.codeworks.dsp.service.impl.BesApiServiceImpl.scheduleCheckModifiedCreatives(..))")
    private void scheduleCheckModifiedCreatives() {}
    @Around("scheduleCheckModifiedCreatives()")
    public List<Integer> doPostMaterialToRtbAtBaiduModify(ProceedingJoinPoint pjp) throws Throwable{
        List<Integer> ids = (ArrayList)pjp.proceed(pjp.getArgs());
        sendAndCheckMaterial(ids);
        return ids;
    }

    @Pointcut("execution(* org.codeworks.dsp.service.impl.CreativesServiceImpl.updateMaterials(..))")
    private void updateMaterials() {}
    @Around("updateMaterials()")
    public List<Material> doDeleteMaterialToRtbAtModify(ProceedingJoinPoint pjp) throws Throwable{
        Object[] args = pjp.getArgs();

        //未更改的原数据
        List<Material> origins = new ArrayList<>();

        Integer argIndex = 2;
        if (args.length>=argIndex && Optional.ofNullable(args[argIndex]).isPresent()) {
            Materials argMaterials = (Materials) args[argIndex];
            if (Optional.ofNullable(argMaterials.getMaterials()).isPresent()){
                List<Material> datas = creativesService.getMaterials(argMaterials.getMaterials().stream().map(m -> m.getId()).collect(Collectors.toList()));
                origins = datas.stream().map(m -> {
                    Material material = new Material();
                    BeanUtils.copyProperties(m, material);
                    return material;
                }).collect(Collectors.toList());
            }
        }

        Set<Material> filters = new HashSet<>();

        //已保存数据
        List<Material> saves = (ArrayList)pjp.proceed(args);

        //过滤<百度审核,上下架>(暂无)
        if (origins.size() == saves.size()){
            for (int i = 0; i < origins.size(); i++) {

                Material oriMaterial = origins.get(i);
                Material saveMaterial = saves.get(i);

                //if(!oriMaterial.getReview().equals(saveMaterial.getReview())
                    //|| !oriMaterial.getStatus().equals(saveMaterial.getStatus()))
                    filters.add(saveMaterial);

            }
        }

        deleteMaterial(new ArrayList<>(filters));

        return saves;
    }






    private void sendAndCheckMaterial(List<Integer> ids){
        if (!Optional.ofNullable(ids).isPresent() || ids.isEmpty()) return;

        List<Material> materials = creativesService.getMaterials(ids);
        rtbApiService.syncMaterials(filterCondition(materials));
    }

    private void deleteMaterial(List<Material> materials){
        if (!Optional.ofNullable(materials).isPresent() || materials.isEmpty()) return;

        rtbApiService.deleteMaterials(materials);
    }

    private List<Material> filterCondition(List<Material> materials){
        //关联活动
        //物料上架状态(暂无)
        //同步状态(暂无)
        //百度审核状态
        return materials.stream()
                .filter(m -> !Optional.ofNullable(m.getCampaign()).isPresent())
                //.filter(m -> m.getStatus().equals(Material.Status.eligible))
                //.filter(m -> m.getSync().equals(Material.Sync.not))
                .filter(m -> m.getReview().equals(Material.Review.eligible))
                .collect(Collectors.toList());
    }
}
