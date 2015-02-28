<?php

namespace Insalade\CommunicationBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolverInterface;

class PostType extends AbstractType
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
            ->add('pushText', 'textarea', array('label' => 'Push Text'))
            ->add('description', 'textarea', array('label' => 'Description'))
            ->add('image')
            ->add('dateStart', 'datetime', array('label' => 'Start Communication Date'))
            ->add('dateEnd', 'datetime', array('label' => 'End Communication Date'))
            ->add('eventStart', 'datetime', array('label' => 'Start Event Date'))
            ->add('eventEnd', 'datetime', array('label' => 'End Event Date'))
        ;
    }
    
    /**
     * @param OptionsResolverInterface $resolver
     */
    public function setDefaultOptions(OptionsResolverInterface $resolver)
    {
        $resolver->setDefaults(array(
            'data_class' => 'Insalade\CommunicationBundle\Entity\Post'
        ));
    }

    /**
     * @return string
     */
    public function getName()
    {
        return 'insalade_communicationbundle_post';
    }
}
