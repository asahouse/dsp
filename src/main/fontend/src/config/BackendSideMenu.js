export default [
    {
        "name": "管理员菜单",
        "children": [
            {
                "name": "控制台",
                "icon": "fa fa-dashboard",
                "link": "/admin/dashboard"
            },
            {
                "name": "广告主",
                "icon": "iconfont icon-guanggaozhu",
                "children":[
                    {
                        "name": "广告主列表",
                        "link": "/admin/advertisers"
                    }
                ]
            },
            {
                "name": "投放活动",
                "icon": "ion-ios-analytics",
                "children":[
                    {
                        "name": "活动列表",
                        "link": "/admin/campaigns"
                    }
                ]
            },
            {
                "name": "素材审核",
                "icon": "iconfont icon-shenhe",
                "children":[
                    {
                        "name": "审核列表",
                        "link": "/admin/review",
                    },
                    {
                        "name": "预览审核",
                        "link": "/admin/review_item",
                    },
                ]
            },
            {
                "name": "数据统计",
                "icon": "fa fa-line-chart",
                "children":[
                    {
                        "name":"RTP 竞价报告",
                        "link": "/admin/reports/rtb",
                        "disabled": true,
                    },
                    {
                        "name":"消费报告",
                        "link": "/admin/reports/consume",
                        "disabled": true,
                    },
                    {
                        "name":"广告主报告",
                        "link": "/admin/reports/advertiser",
                        "disabled": true,
                    },
                    {
                        "name":"RTB分创意报告",
                        "link": "/admin/reports/creativeRTB",
                        "disabled": true,
                    },

                    {
                        "name":"消费信息统计",
                        "link": "/admin/stat/consume",
                        "disabled": true,
                    },
                    {
                        "name":"RTB情况信息统计",
                        "link": "/admin/stat/rtb",
                        "disabled": true,
                    },
                    {
                        "name":"监测信息统计",
                        "link": "/admin/stat/monitor",
                        "disabled": true,
                    },
                    {
                        "name":"活动信息统计",
                        "link": "/admin/stat/campaign",
                        "disabled": true,
                    },
                ]
            },
            {
                "name": "数字营销工具",
                "icon": "fa fa-th-large",
                "children":[
                    {
                        "name": "人群画像分析",
                        "link": "/admin/tools/analysis"
                    },
                    {
                        "disabled": true,
                        "name": "网站流量源统计",
                        "link": "/admin/tools/tongji"
                    },
                    {
                        "disabled": true,
                        "name": "搜索推广",
                        "link": "/admin/tools/seo"
                    },
                    {
                        "disabled": true,
                        "name": "地域风向标",
                        "link": "/admin/tools/district"
                    },
                    {
                        "disabled": true,
                        "name": "舆情监控",
                        "link": "/admin/tools/monitoring"
                    },
                ]
            },
            {
                "name": "投放活动监测",
                "icon": "fa fa-area-chart",
                "children":[
                    {
                        "name":"概览",
                        "link": "/admin/bdtj",
                    },
                    {
                        "name":"访客分析",
                        "disabled": true,
                        "link": {
                            path: "/admin/bdtj_visit",
                            exact: true
                        },
                    }
                ]
            },
        ]
    }
]