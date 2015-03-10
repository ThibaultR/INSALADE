<?php

namespace Insalade\CommunicationBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolverInterface;

class PushType extends AbstractType
{
    /**
     * @param FormBuilderInterface $builder
     * @param array $options
     */
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('association')
            ->add('title')
            ->add('pushText')
            ->add('dateStart', 'datetime', array('label' => 'Start Communication Date'))
        ;
    }
    
    /**
     * @param OptionsResolverInterface $resolver
     */
    public function setDefaultOptions(OptionsResolverInterface $resolver)
    {
        $resolver->setDefaults(array(
            'data_class' => 'Insalade\CommunicationBundle\Entity\Push'
        ));
    }

    /**
     * @return string
     */
    public function getName()
    {
        return 'insalade_communicationbundle_push';
    }
}
